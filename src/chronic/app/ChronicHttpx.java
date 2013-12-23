/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Subscriber;
import chronic.entity.Topic;
import chronic.entity.User;
import chronic.entitykey.CertKey;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.SubscriberKey;
import chronic.entitytype.ChronicDatabaseInjectable;
import chronic.entitytype.OrgRoleType;
import chronic.persona.PersonaException;
import chronic.persona.PersonaUserInfo;
import chronic.persona.PersonaVerifier;
import chronicexp.jdbc.CachingJdbcDatabase;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMapException;
import vellum.security.Certificates;
import vellum.security.X509Certificates;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpx extends Httpx {
    Logger logger = LoggerFactory.getLogger(ChronicHttpx.class);

    public ChronicApp app;
    public CachingJdbcDatabase db;

    public ChronicHttpx(ChronicApp app, HttpExchange delegate) {
        super(delegate);
        this.app = app;
    }

    public String getEmail() throws JMapException, IOException, PersonaException {
        if (ChronicCookie.matches(getCookieMap())) {
            ChronicCookie cookie = new ChronicCookie(getCookieMap());
            if (cookie.getEmail() != null) {
                if (app.properties.isTesting()) {
                    if (getReferer().endsWith("/mimic") 
                            && app.properties.getMimicEmail() != null
                            && app.properties.isAdmin(cookie.getEmail())) {
                        return app.properties.getMimicEmail();
                    } else {
                        return cookie.getEmail();
                    }
                }
                PersonaUserInfo userInfo = new PersonaVerifier(app, cookie).
                        getUserInfo(getHostUrl(), cookie.getAssertion());
                if (cookie.getEmail().equals(userInfo.getEmail())) {
                    return userInfo.getEmail();
                }
            }
        }
        logger.warn("getEmail cookie {}", getCookieMap());
        setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
        throw new PersonaException("no verified email");
    }
        
    public void setDatabase(CachingJdbcDatabase db) {
        this.db = db;
    }
    
    public void injectDatabase(Collection<? extends ChronicDatabaseInjectable> collection) 
            throws Exception {
        logger.info("injectDatabase collection {}", collection);
        for (ChronicDatabaseInjectable element : collection) {
            logger.info("injectDatabase element {} {}", element.getClass(), element);
            element.inject(db);
        }
    }

    public Cert persistCert() throws StorageException, CertificateException,
            SSLPeerUnverifiedException {
        X509Certificate certificate = getPeerCertficate();
        String remoteHostAddress = getRemoteHostAddress();
        String encoded = X509Certificates.getEncodedPublicKey(certificate);
        String commonName = Certificates.getCommonName(certificate.getSubjectDN());
        String orgDomain = Certificates.getOrg(certificate.getSubjectDN());
        String orgUnit = Certificates.getOrgUnit(certificate.getSubjectDN());
        if (!app.getProperties().getAllowedOrgDomains().contains(orgDomain)) {
            throw new CertificateException("org not allowed: " + orgDomain);
        } else if (!app.getProperties().getAllowedAddresses().contains(remoteHostAddress)) {
            logger.info("remote hostAddress {}", remoteHostAddress);
        }
        boolean enabled = false;
        Org org = db.org().find(orgDomain);
        if (org == null) {
            enabled = true;
            org = new Org(orgDomain);
            db.org().persist(org);
            logger.info("persist org {}", org);
        }
        CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);
        Cert cert = db.cert().find(certKey);
        if (cert == null) {
            cert = new Cert(certKey);
            cert.setEnabled(enabled);
            cert.setEncoded(encoded);
            cert.setAddress(remoteHostAddress);
            cert.setTimestamp(System.currentTimeMillis());
            cert.setAddress(remoteHostAddress);
            db.cert().persist(cert);
            logger.info("certificate {}", certKey);
        } else if (!cert.getEncoded().equals(encoded)) {
            logger.warn("invalid public key {}", certKey);
        } else if (!cert.isEnabled()) {
            logger.warn("cert disabled {}", certKey);
        } else if (!cert.getAddress().equals(remoteHostAddress)) {
            logger.warn("host address {}", remoteHostAddress);
        }
        cert.setOrg(org);
        return cert;
    }

    public User persistUser(String email) throws StorageException {
        logger.info("handle {}", email);
        User user = db.user().find(email);
        if (user == null) {
            user = new User(email);
            db.user().persist(user);
        }
        return user;
    }
    
    public void persistCertSubscriber(Cert cert, String email) 
            throws StorageException {
        logger.info("handle {} {}", cert, email);
        User user = db.user().find(email);
        if (user == null) {
            user = new User(email);
            db.user().persist(user);
        }
        for (Topic topic : db.topic().list(cert.getKey())) {
            persistTopicSubscriber(topic, email);
        }
        
    }
    
    public OrgRole persistOrgRole(Cert cert, String email, OrgRoleType roleType) 
            throws StorageException {
        logger.info("enroll {} {}", cert, email);
        User user = persistUser(email);        
        logger.info("user {}", user);
        OrgRoleKey orgRoleKey = new OrgRoleKey(cert.getOrgDomain(), email, roleType);
        OrgRole orgRole = db.role().find(orgRoleKey);
        if (orgRole == null) {
            orgRole = new OrgRole(orgRoleKey);
            orgRole.setEnabled(true);
            db.role().persist(orgRole);
        }
        return orgRole;
    }
    
    public Topic persistTopic(Cert cert, String topicLabel)
            throws StorageException {
        logger.info("handle {} {}", topicLabel, cert);
        assert(cert.getId() != null);
        CertTopicKey key = new CertTopicKey(cert.getId(), topicLabel);
        Topic topic = db.topic().find(key);
        if (topic == null) {
            topic = new Topic(key);            
            db.topic().persist(topic);
        }
        topic.setCert(cert);
        return topic;
    }
    
    public Subscriber persistTopicSubscriber(Topic topic, String email) 
            throws StorageException {
        SubscriberKey key = new SubscriberKey(topic.getId(), email);
        Subscriber subscriber = db.sub().find(key);
        if (subscriber == null) {
            subscriber = new Subscriber(key);
            db.sub().persist(subscriber);
        }
        return subscriber;
    }
}

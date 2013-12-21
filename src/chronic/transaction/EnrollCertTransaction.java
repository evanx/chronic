/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.api.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entitykey.CertKey;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.security.Certificates;
import vellum.security.X509Certificates;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollCertTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollCertTransaction.class);
    
    public Cert handle(ChronicHttpx httpx) throws StorageException, CertificateException,
            SSLPeerUnverifiedException {
        X509Certificate certificate = httpx.getPeerCertficate();
        String hostAddress = httpx.getRemoteHostAddress();
        String encoded = X509Certificates.getEncodedPublicKey(certificate);
        String commonName = Certificates.getCommonName(certificate.getSubjectDN());
        String orgDomain = Certificates.getOrg(certificate.getSubjectDN());
        String orgUnit = Certificates.getOrgUnit(certificate.getSubjectDN());
        if (!httpx.app.getProperties().getAllowedOrgDomains().contains(orgDomain)) {
            throw new CertificateException("org not allowed: " + orgDomain);
        } else if (!httpx.app.getProperties().getAllowedAddresses().contains(hostAddress)) {
            logger.info("remote hostAddress {}", hostAddress);
        }
        CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);
        Cert cert = httpx.db.cert().find(certKey);
        if (cert == null) {
            cert = new Cert(certKey);
            cert.setEncoded(encoded);
            cert.setAddress(hostAddress);
            httpx.db.cert().add(cert);
            logger.info("certificate {}", certKey);
        } else if (!cert.getEncoded().equals(encoded)) {
            logger.warn("invalid public key {}", certKey);
        } else if (!cert.isEnabled()) {
            logger.warn("cert disabled {}", certKey);
        } else if (!cert.getAddress().equals(hostAddress)) {
            logger.warn("host address {}", hostAddress);
        }
        cert.setTimestamp(System.currentTimeMillis());
        Org org = httpx.db.org().find(cert.getOrgDomain());
        if (org == null) {
            org = new Org(cert.getOrgDomain());
            httpx.db.org().add(org);
            logger.info("insert org {}", org);
        }
        cert.setOrg(org);
        return cert;
    }
}

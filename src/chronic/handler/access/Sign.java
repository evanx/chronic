/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.app.SigningInfo;
import chronic.app.ChronicHttpx;
import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.IssuedCert;
import chronic.entity.Org;
import chronic.entitykey.CertKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellumcert.Pems;
import vellumcert.CertReq;
import vellumcert.CertReqs;

/**
 *
 * @author evan.summers
 */
public class Sign implements PlainHttpxHandler {
    
    private static Logger logger = LoggerFactory.getLogger(Sign.class); 
    
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        logger.info("handle");
        SigningInfo signingInfo = app.getSigningInfo();
        String certReqPem = httpx.readString();
        CertReq certReq = CertReqs.create(certReqPem);
        logger.info("certReq {}", certReq);
        CertKey certKey = new CertKey(certReq.getSubject());
        logger.info("certKey {}", certKey);
        Org org = es.persistOrg(certKey.getOrgDomain());
        logger.info("org {}", org);
        for (IssuedCert cert : es.selectIssuedCert(certKey)) {
            if (cert.isEnabled()) {
                logger.warn("issued cert exists {}", certKey);
                return "ERROR: certificate already exists" ;
            } else {
                es.remove(cert);
            }
        }
        IssuedCert cert = new IssuedCert(certKey);
        cert.setIssued(Calendar.getInstance());
        Calendar expired = Calendar.getInstance();
        expired.add(Calendar.DATE, signingInfo.getValidityDays());
        cert.setExpired(expired);
        cert.setEnabled(false);
        es.persist(cert);
        X509Certificate signedCert = CertReqs.sign(certReq, signingInfo.getSigningKey(), 
                signingInfo.getSigningCert(), cert.getIssued().getTime(), 
                cert.getExpired().getTime(), cert.getId());
        logger.info("signedCert {}", signedCert);
        return Pems.buildCertPem(signedCert);
    }

}

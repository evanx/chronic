/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entitykey.CertKey;
import java.security.cert.CertificateException;
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
public class VerifyCertTransaction {
    
    static Logger logger = LoggerFactory.getLogger(VerifyCertTransaction.class);
    
    public Cert handle(ChronicApp app, String hostAddress, X509Certificate certificate) 
            throws StorageException, CertificateException {
        String encoded = X509Certificates.getEncodedPublicKey(certificate);
        String commonName = Certificates.getCommonName(certificate.getSubjectDN());
        String orgUrl = Certificates.getOrg(certificate.getSubjectDN());
        String orgUnit = Certificates.getOrgUnit(certificate.getSubjectDN());
        if (!app.getProperties().getAllowedOrgUrls().contains(orgUrl)) {
            throw new CertificateException("org not allowed: " + orgUrl);
        } else if (!app.getProperties().getAllowedAddresses().contains(hostAddress)) {
            logger.warn("remote hostAddress {}", hostAddress);
        }
        CertKey certKey = new CertKey(orgUrl, orgUnit, commonName);
        Cert cert = app.storage().cert().select(certKey);
        if (cert == null) {
            cert = new Cert(certKey);
            cert.setEncoded(encoded);
            cert.setAddress(hostAddress);
            app.storage().cert().insert(cert);
            logger.info("certificate {}", certKey);
        } else if (!cert.getEncoded().equals(encoded)) {
            logger.warn("invalid public key {}", certKey);
        } else if (!cert.isEnabled()) {
            logger.warn("cert disabled {}", certKey);
        } else if (!cert.getAddress().equals(hostAddress)) {
            logger.warn("host address {}", hostAddress);
        }
        cert.setTimestamp(System.currentTimeMillis());
        Org org = app.storage().org().select(cert.getOrgUrl());
        if (org == null) {
            org = new Org(cert.getOrgUrl());
            app.storage().org().insert(org);
            logger.info("insert org {}", org);
        }
        return cert;
    }
}

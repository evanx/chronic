/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.handler.app;

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;
import vellum.security.DnameType;
import vellum.security.X509Certificates;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class CertInfo implements CertKeyed {
    X509Certificate certificate;
    String remoteHostAddress;
    String commonName;
    String orgDomain;
    String orgUnit;
    String encoded;
    
    public CertInfo(Httpx httpx) throws StorageException, CertificateException,
            SSLPeerUnverifiedException {
        certificate = httpx.getPeerCertficate();
        remoteHostAddress = httpx.getRemoteHostAddress();
        encoded = X509Certificates.getEncodedPublicKey(certificate);
        commonName = Certificates.get(DnameType.CN, certificate.getSubjectDN());
        orgDomain = Certificates.get(DnameType.O, certificate.getSubjectDN());
        orgUnit = Certificates.get(DnameType.OU, certificate.getSubjectDN());
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public String getRemoteHostAddress() {
        return remoteHostAddress;
    }

    public void setRemoteHostAddress(String remoteHostAddress) {
        this.remoteHostAddress = remoteHostAddress;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrgDomain() {
        return orgDomain;
    }

    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgDomain, orgUnit, commonName);
    }

}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.storage;

import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.entity.AbstractIdEntity;
import vellum.security.Certificates;
import vellum.security.PemCerts;

/**
 *
 * @author evan.summers
 */
public final class Cert extends AbstractIdEntity {
    Long id;
    Long orgId;
    String name;
    String subject;
    String cert;
    String ipAddress;
    boolean enabled = true;
    Date inserted = new Date();
    Date updated = new Date();

    public Cert() {
    }
    
    public void setCert(X509Certificate cert) {
        this.cert = PemCerts.buildCertPem(cert);
        this.subject = cert.getSubjectDN().getName();
        this.name = Certificates.getCommonName(subject);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
    
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getOrgId() {
        return orgId;
    }
    
    @Override
    public String toString() {
        return subject;
    }
}

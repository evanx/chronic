/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgKeyed;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import vellum.entity.ComparableEntity;
import vellum.jx.JMap;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
@Entity
@Table(name = "issued_cert")
public class IssuedCert extends ComparableEntity implements CertKeyed, 
        OrgKeyed, Enabled, Serializable {

    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cert_id")
    Long id;
    
    @Column(name = "org_domain")
    String orgDomain;
    
    @Column(name = "org_unit")
    String orgUnit;
    
    @Column(name = "common_name")
    String commonName;

    @Column()
    boolean enabled;
    
    @Column(name = "issued")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar issued;

    @Column(name = "revoked")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar revoked;
    
    @Column(name = "expired")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar expired;
    
    @ManyToOne()    
    @JoinColumn(name = "org_domain", referencedColumnName = "org_domain", insertable = false, updatable = false)
    Org org;
    
    public IssuedCert() {
    }

    public IssuedCert(CertKey key) {
        this(key.getOrgDomain(), key.getOrgUnit(), key.getCommonName());
    }
    
    public IssuedCert(String orgDomain, String orgUnit, String commonName) {
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        issued = Calendar.getInstance();
    }

    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public CertKey getCertKey() {
        return new CertKey(orgDomain, orgUnit, commonName);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Org getOrg() {
        return org;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }
    
    public String getOrgDomain() {
        return orgDomain;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }
    
    public String getOrgUnit() {
        return orgUnit;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public String getCommonName() {
        return commonName;
    }

    public void setIssued(Calendar issued) {
        this.issued = issued;
    }

    public Calendar getIssued() {
        return issued;
    }

    public void setExpired(Calendar expired) {
        this.expired = expired;
    }

    public Calendar getExpired() {
        return expired;
    }

    public void setRevoked(Calendar revoked) {
        this.revoked = revoked;
    }

    public Calendar getRevoked() {
        return revoked;
    }            
    
    public JMap getKeyMap() {
        JMap map = new JMap();
        map.put("issuedCertId", id);
        map.put("orgDomain", orgDomain);
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        return map;
    }
    
    @Override
    public String toString() {
        return getKeyMap().toString();
    }

}

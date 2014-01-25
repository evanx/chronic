/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.CertKeyed;
import chronic.entitytype.CertActionType;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import vellum.entity.ComparableEntity;
import vellum.format.CalendarFormats;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
@Entity
public class Cert extends ComparableEntity implements OrgKeyed, CertKeyed, Enabled, JMapped, Serializable {

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

    @Column(length = 4096)
    String encoded;
    
    @Column()
    boolean enabled;
    
    @Column(name = "acquired")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar acquired;

    @Column(name = "revoked")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar revoked;
    
    @Column()
    String address;
    
    @ManyToOne()    
    @JoinColumn(name = "org_domain", referencedColumnName = "org_domain", insertable = false, updatable = false)
    Org org;
    
    public Cert() {
    }

    public Cert(CertKey key) {
        this(key.getOrgDomain(), key.getOrgUnit(), key.getCommonName());
    }
    
    public Cert(String orgDomain, String orgUnit, String commonName) {
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        acquired = Calendar.getInstance();
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
    
    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Calendar getAcquired() {
        return acquired;
    }

    public void setAcquired(Calendar acquired) {
        this.acquired = acquired;
    }

    public void setRevoked(Calendar revoked) {
        this.revoked = revoked;
    }

    public Calendar getRevoked() {
        return revoked;
    }        
    
    @Override
    public JMap getMap() {
        JMap map = getKeyMap();
        map.put("enabled", enabled);
        map.put("address", address);
        map.put("timestampLabel", CalendarFormats.timeFormat.format(acquired.getTime()));
        map.put("address", address);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        return map;
    }

    private CertActionType getAction() {
        return enabled ? CertActionType.REVOKE : CertActionType.GRANT;
    }
    
    public JMap getKeyMap() {
        JMap map = new JMap();
        map.put("certId", id);
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

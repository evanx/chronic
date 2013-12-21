/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgKey;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import vellum.data.Patterns;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.security.Certificates;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;
import vellum.validation.ValidationException;
import vellum.validation.ValidationExceptionType;

/**
 *
 * @author evan.summers
 */
@Entity
public class Org extends AbstractIdEntity implements OrgKeyed, Enabled, Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "org_domain")
    String orgDomain;
    
    @Column(name = "org_name")
    String orgName;

    @Column()
    String label;

    @Column()
    String region;

    @Column()
    String locality;

    @Column()
    String country;

    @Column()
    boolean enabled;
            
    public Org() {
    }

    public Org(String url, String orgName) {
        this.orgDomain = url;
        this.orgName = orgName;
    }

    public Org(String url) {
        this(url, url);
    }
      
    
    public Org(JMap map) throws JMapException {
        orgDomain = map.getString("url");
        orgName = map.getString("orgName");
        label = map.getString("label");
        region = map.getString("region");
        locality = map.getString("locality");
        country = map.getString("country");
        if (orgName == null) {
            orgName = orgDomain;
        }
        if (label == null) {
            label = orgName;
        }
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("orgId", id);
        map.put("orgName", orgName);
        map.put("label", label);
        map.put("url", orgDomain);
        map.put("region", region);
        map.put("locality", locality);
        map.put("country", country);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public String getKey() {
        return orgDomain;
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
        

    public String getOrgName() {
        return orgName;
    }
    
    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }
    
    public String getOrgDomain() {
        return orgName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String formatDname(String cn, String ou) {
        return Certificates.formatDname(cn, ou, orgName, locality, region, country);
    }
    
    public void validate() throws ValidationException {
        if (!Patterns.matchesDomain(orgDomain)) {
            throw new ValidationException(ValidationExceptionType.INVALID_URL, orgDomain);
        }
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}

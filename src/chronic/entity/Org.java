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
import javax.persistence.Id;
import vellum.data.Patterns;
import vellum.entity.ComparableEntity;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.type.Enabled;
import vellum.validation.ValidationException;
import vellum.validation.ValidationExceptionType;

/**
 *
 * @author evan.summers
 */
@Entity
public class Org extends ComparableEntity implements OrgKeyed, Enabled, Serializable {
    
    @Id
    @Column(name = "org_domain")
    String orgDomain;

    @Column()
    String server = "secure.chronica.co";
    
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

    public Org(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    public Org(JMap map) throws JMapException {
        orgDomain = map.getString("org_domain");
        label = map.getString("label");
        region = map.getString("region");
        locality = map.getString("locality");
        country = map.getString("country");
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("orgDomain", orgDomain);
        map.put("label", label);
        map.put("locality", locality);
        map.put("region", region);
        map.put("country", country);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public String getId() {
        return orgDomain;
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
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
        return orgDomain;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void validate() throws ValidationException {
        if (!Patterns.matchesDomain(orgDomain)) {
            throw new ValidationException(ValidationExceptionType.INVALID_DOMAIN, orgDomain);
        }
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.datatype.Patterns;
import vellum.parameter.StringMap;
import vellum.security.Certificates;
import vellum.storage.AbstractEntity;
import vellum.validation.ValidationException;
import vellum.validation.ValidationExceptionType;

/**
 *
 * @author evan.summers
 */
public final class Org extends AbstractEntity {
    Long id;
    String orgName;
    String label;
    String url;
    String region;
    String locality;
    String country;
    boolean enabled = true;
            
    public Org() {
    }

    public Org(String orgName) {
        this.orgName = orgName;
    }
      
    public Org(StringMap map) {
        update(map);
    }

    public void update(Org bean) {
        update(bean.getStringMap());
    }
    
    public void update(StringMap map) {
        url = map.get("url");
        orgName = map.get("orgName");
        label = map.get("label");
        region = map.get("region");
        locality = map.get("locality");
        country = map.get("country");
        if (orgName == null) {
            orgName = url;
        }
        if (label == null) {
            label = orgName;
        }
    }
    
    public StringMap getStringMap() {
        StringMap map = new StringMap();
        map.put("orgId", id);
        map.put("orgName", orgName);
        map.put("label", label);
        map.put("url", url);
        map.put("region", region);
        map.put("locality", locality);
        map.put("country", country);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public String getKey() {
        return url;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String formatDname(String cn, String ou) {
        return Certificates.formatDname(cn, ou, orgName, locality, region, country);
    }
    
    public void validate() throws ValidationException {
        if (!Patterns.matchesUrl(url)) {
            throw new ValidationException(ValidationExceptionType.INVALID_URL, url);
        }
    }

    public String toJson() {
        return getStringMap().toJson();
    }
    
    @Override
    public String toString() {
        return getStringMap().toJson();
    }
}

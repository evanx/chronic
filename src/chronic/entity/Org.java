/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.datatype.Patterns;
import vellum.jx.JMap;
import vellum.jx.JMapException;
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

    public Org(String url, String orgName) {
        this.url = url;
        this.orgName = orgName;
    }

    public Org(String url) {
        this(url, url);
    }
      
    
    public Org(JMap map) throws JMapException {
        update(map);
    }

    public void update(JMap map) throws JMapException {
        url = map.getString("url");
        orgName = map.getString("orgName");
        label = map.getString("label");
        region = map.getString("region");
        locality = map.getString("locality");
        country = map.getString("country");
        if (orgName == null) {
            orgName = url;
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

    public String getOrgUrl() {
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

    @Override
    public String toString() {
        return getMap().toString();
    }
}

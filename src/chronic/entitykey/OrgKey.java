/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.util.ComparableValue;

/**
 *
 * @author evan.summers
 */
public class OrgKey extends ComparableValue {
    String orgUrl;

    public OrgKey(String orgUrl) {
        super(orgUrl);
        this.orgUrl = orgUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        
    
}


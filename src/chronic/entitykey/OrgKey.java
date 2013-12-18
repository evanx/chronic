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
    String orgDomain;

    public OrgKey(String orgDomain) {
        super(orgDomain);
        this.orgDomain = orgDomain;
    }

    public String getOrgDomain() {
        return orgDomain;
    }        
    
}


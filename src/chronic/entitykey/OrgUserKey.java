/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class OrgUserKey extends ComparableTuple {
    String orgDomain;
    String email;

    public OrgUserKey(String orgDomain, String email) {
        super(orgDomain, email);        
        this.orgDomain = orgDomain;
        this.email = email;
    }

    public String getOrgDomain() {
        return orgDomain;
    }        

    public String getEmail() {
        return email;
    }        
}


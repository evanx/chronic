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
public class OrgRoleKey extends ComparableTuple {
    String orgUrl;
    String email;

    public OrgRoleKey(String orgUrl, String email) {
        super(orgUrl, email);        
        this.orgUrl = orgUrl;
        this.email = email;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        

    public String getEmail() {
        return email;
    }        
}


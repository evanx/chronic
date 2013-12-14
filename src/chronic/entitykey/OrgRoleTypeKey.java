/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import chronic.entitytype.OrgRoleType;
import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class OrgRoleTypeKey extends ComparableTuple {
    String orgUrl;
    OrgRoleType roleType;

    public OrgRoleTypeKey(String orgUrl, OrgRoleType roleType) {
        super(orgUrl, roleType);        
        this.orgUrl = orgUrl;
        this.roleType = roleType;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        

    public OrgRoleType getRoleType() {
        return roleType;
    }
        
}


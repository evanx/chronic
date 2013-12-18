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
    String orgDomain;
    OrgRoleType roleType;

    public OrgRoleTypeKey(String orgDomain, OrgRoleType roleType) {
        super(orgDomain, roleType);        
        this.orgDomain = orgDomain;
        this.roleType = roleType;
    }

    public String getOrgDomain() {
        return orgDomain;
    }        

    public OrgRoleType getRoleType() {
        return roleType;
    }
        
}


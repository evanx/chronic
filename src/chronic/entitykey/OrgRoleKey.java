/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import chronic.entitytype.OrgRoleType;
import vellum.data.ComparableTuple;
import vellum.jx.JMap;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public class OrgRoleKey extends ComparableTuple {
    String orgUrl;
    String email;
    OrgRoleType roleType;

    public OrgRoleKey(JMap map, String email) throws JMapException {
        this(map.getString("orgUrl"), email,
                OrgRoleType.valueOf(map.getString("roleType")));
    }
    
    public OrgRoleKey(String orgUrl, String email, OrgRoleType roleType) {
        super(orgUrl, email, roleType);        
        this.orgUrl = orgUrl;
        this.email = email;
        this.roleType = roleType;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        

    public String getEmail() {
        return email;
    }
    
    public OrgRoleType getRoleType() {
        return roleType;
    }
        
}


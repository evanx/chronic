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
    String orgDomain;
    String email;
    OrgRoleType roleType;

    public OrgRoleKey(JMap map) throws JMapException {
        this(map.getString("orgDomain"), map.getString("email"),
                OrgRoleType.valueOf(map.getString("roleType")));
    }
    
    public OrgRoleKey(String orgDomain, String email, OrgRoleType roleType) {
        super(orgDomain, email, roleType);        
        this.orgDomain = orgDomain;
        this.email = email;
        this.roleType = roleType;
    }

    public String getOrgDomain() {
        return orgDomain;
    }        

    public String getEmail() {
        return email;
    }
    
    public OrgRoleType getRoleType() {
        return roleType;
    }
        
}


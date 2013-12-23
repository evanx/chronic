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
public class PersonRoleTypeKey extends ComparableTuple {
    String email;
    OrgRoleType roleType;

    public PersonRoleTypeKey(String email, OrgRoleType roleType) {
        super(email, roleType);        
        this.email = email;
        this.roleType = roleType;
    }

    public String getEmail() {
        return email;
    }
    
    public OrgRoleType getRoleType() {
        return roleType;
    }
        
}


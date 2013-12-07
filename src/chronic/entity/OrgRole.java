/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.storage.AbstractIdEntity;
import vellum.util.Args;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class OrgRole extends AbstractIdEntity {
    Long id;
    AdminUserRoleType role;    
    String email;
    String orgName;
            
    transient AdminUser user; 
    transient Org org;
    
    public OrgRole(AdminUser user, Org org, AdminUserRoleType role) {
        this.user = user;
        this.org = org;
        this.role = role;
        email = user.getEmail();
        orgName = org.getOrgName();
    }
        
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Comparable getKey() {
        return Comparables.tuple(email, orgName);
    }

    public AdminUserRoleType getRole() {
        return role;
    }
    
    public AdminUser getUser() {
        return user;
    }

    public Org getOrg() {
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(email, orgName, role);
    }
    
    
}

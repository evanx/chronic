/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.storage.AbstractIdEntity;

/**
 *
 * @author evan.summers
 */
public class OrgRole extends AbstractIdEntity {
    Long id;
    AdminUser user; 
    Org org;
    AdminUserRoleType role;

    public OrgRole(AdminUser user, Org org, AdminUserRoleType role) {
        this.user = user;
        this.org = org;
        this.role = role;
    }
        
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getKey() {
        return id;
    }

    public AdminUser getUser() {
        return user;
    }

    public Org getOrg() {
        return org;
    }    
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.storage;

import vellum.entity.LongIdEntity;

/**
 *
 * @author evan.summers
 */
public class OrgRole implements LongIdEntity {
    Long id;
    AdminUser user; 
    Org org;
    AdminUserRole role;

    public OrgRole(AdminUser user, Org org, AdminUserRole role) {
        this.user = user;
        this.org = org;
        this.role = role;
    }
        
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public AdminUser getUser() {
        return user;
    }

    public Org getOrg() {
        return org;
    }
    
}

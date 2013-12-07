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
    String orgUrl;
    String email;
    AdminUserRoleType role;    
            
    transient Org org;
    transient AdminUser user; 
    
    public OrgRole(AdminUser user, Org org, AdminUserRoleType role) {
        this.org = org;
        this.user = user;
        this.role = role;
        orgUrl = org.getOrgUrl();
        email = user.getEmail();
    }
        
    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, email);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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
        return Args.format(orgUrl, email, role);
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.ChronicApp;
import vellum.storage.AbstractIdEntity;
import vellum.storage.StorageException;
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
    ChronicApp app; 
    
    transient Org org;
    transient AdminUser user; 

    public OrgRole(ChronicApp app, String orgUrl, String email) {
        this.app = app;
        this.orgUrl = orgUrl;
        this.email = email;
    }
    
    public OrgRole(Org org, AdminUser user, AdminUserRoleType role) {
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

    public Long getId() {
        return id;
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
    
    public AdminUser getUser() throws StorageException {
        if (user == null && email != null) {
            user = app.getStorage().getAdminUserStorage().find(email);
        }
        return user;
    }

    public Org getOrg() throws StorageException {
        if (org == null && orgUrl != null) {
            org = app.getStorage().getOrgStorage().find(orgUrl);
        }
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(orgUrl, email, role);
    }
}

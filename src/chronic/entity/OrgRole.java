/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.ChronicApp;
import vellum.jx.JMap;
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
    UserRoleType role;    
    ChronicApp app; 
    
    transient Org org;
    transient User user; 

    public OrgRole(ChronicApp app, String orgUrl, String email) {
        this.app = app;
        this.orgUrl = orgUrl;
        this.email = email;
    }
    
    public OrgRole(Org org, User user, UserRoleType role) {
        this.org = org;
        this.user = user;
        this.role = role;
        orgUrl = org.getOrgUrl();
        email = user.getEmail();
    }

    public JMap getMap() throws StorageException {
        return new JMap(
                JMap.entry("orgName", getOrg().getOrgName()),
                JMap.entry("email", email),
                JMap.entry("role", role));
    }
    
    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, email, role);
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

    public UserRoleType getRole() {
        return role;
    }
    
    public User getUser() throws StorageException {
        if (user == null && email != null) {
            user = app.getStorage().getUserStorage().find(email);
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

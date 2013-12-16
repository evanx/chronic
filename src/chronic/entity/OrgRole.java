/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.OrgKeyed;
import chronic.entitykey.UserKeyed;
import chronic.entitykey.UserKey;
import chronic.entitykey.OrgKey;
import chronic.entitytype.OrgRoleType;
import chronic.ChronicApp;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.OrgRoleKeyed;
import chronic.entitykey.OrgUserKey;
import chronic.entitykey.OrgUserKeyed;
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgRoleTypeKeyed;
import chronic.entitykey.UserRoleTypeKey;
import chronic.entitykey.UserRoleTypeKeyed;
import chronic.entitytype.OrgRoleAction;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.AbstractIdEntity;
import vellum.storage.StorageException;
import vellum.type.Enabled;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class OrgRole extends AbstractIdEntity implements UserKeyed, UserRoleTypeKeyed,
        OrgKeyed, OrgUserKeyed, OrgRoleKeyed, OrgRoleTypeKeyed, Enabled {
    Long id;
    String orgUrl;
    String email;
    OrgRoleType roleType;
    ChronicApp app; 
    boolean enabled = false;
    
    transient Org org;
    transient User user; 

    public OrgRole(ChronicApp app, String orgUrl, String email) {
        this.app = app;
        this.orgUrl = orgUrl;
        this.email = email;
    }
    
    public OrgRole(Org org, User user, OrgRoleType roleType) {
        this.org = org;
        this.user = user;
        this.roleType = roleType;
        orgUrl = org.getOrgUrl();
        email = user.getEmail();
    }

    @Override
    public OrgRoleKey getKey() {
        return getOrgRoleKey();
    }
    
    @Override
    public OrgRoleKey getOrgRoleKey() {
        return new OrgRoleKey(orgUrl, email, roleType);
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgUrl);
    }

    
    @Override
    public UserKey getUserKey() {
        return new UserKey(email);
    }

    @Override
    public OrgUserKey getOrgUserKey() {
        return new OrgUserKey(orgUrl, email);
    }

    @Override
    public OrgRoleTypeKey getOrgRoleTypeKey() {
        return new OrgRoleTypeKey(orgUrl, roleType);
    }

    @Override
    public UserRoleTypeKey getUserRoleTypeKey() {
        return new UserRoleTypeKey(email, roleType);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public JMap getMap() throws StorageException {
        return new JMap(
                JMaps.entry("orgName", getOrg().getOrgName()),
                JMaps.entry("email", email),
                JMaps.entry("role", roleType),
                JMaps.entry("action", getAction()),
                JMaps.entry("actionLabel", getAction().getLabel()),
                JMaps.entry("roleLabel", roleType.getLabel()));
    }
    
    private OrgRoleAction getAction() {
        return enabled ? OrgRoleAction.REVOKE : OrgRoleAction.CONFIRM;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
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

    public OrgRoleType getRoleType() {
        return roleType;
    }
    
    public User getUser() throws StorageException {
        if (user == null && email != null) {
            user = app.storage().user().find(email);
        }
        return user;
    }

    public Org getOrg() throws StorageException {
        if (org == null && orgUrl != null) {
            org = app.storage().org().find(orgUrl);
        }
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(orgUrl, email, roleType);
    }    
}

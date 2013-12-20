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
import chronic.app.ChronicApp;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.OrgRoleKeyed;
import chronic.entitykey.OrgUserKey;
import chronic.entitykey.OrgUserKeyed;
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgRoleTypeKeyed;
import chronic.entitykey.UserRoleTypeKey;
import chronic.entitykey.UserRoleTypeKeyed;
import chronic.entitytype.OrgRoleActionType;
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
    String orgDomain;
    String email;
    OrgRoleType roleType;
    ChronicApp app; 
    boolean enabled = false;
    
    transient Org org;
    transient User user; 

    public OrgRole(ChronicApp app, String orgDomain, String email) {
        this.app = app;
        this.orgDomain = orgDomain;
        this.email = email;
    }
    
    public OrgRole(Org org, User user, OrgRoleType roleType) {
        this.org = org;
        this.user = user;
        this.roleType = roleType;
        orgDomain = org.getOrgDomain();
        email = user.getEmail();
    }

    public OrgRole(OrgRoleKey key) {
        this.orgDomain = key.getOrgDomain();
        this.email = key.getEmail();
        this.roleType = key.getRoleType();
    }
    
    @Override
    public OrgRoleKey getKey() {
        return getOrgRoleKey();
    }
    
    @Override
    public OrgRoleKey getOrgRoleKey() {
        return new OrgRoleKey(orgDomain, email, roleType);
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
    }

    
    @Override
    public UserKey getUserKey() {
        return new UserKey(email);
    }

    @Override
    public OrgUserKey getOrgUserKey() {
        return new OrgUserKey(orgDomain, email);
    }

    @Override
    public OrgRoleTypeKey getOrgRoleTypeKey() {
        return new OrgRoleTypeKey(orgDomain, roleType);
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
                JMaps.entryValue("orgDomain", orgDomain),
                JMaps.entryValue("email", email),
                JMaps.entryValue("roleType", roleType),
                JMaps.entryValue("roleTypeLabel", roleType.getLabel()),
                JMaps.entryValue("action", getAction()),
                JMaps.entryValue("actionLabel", getAction().getLabel()));
    }
    
    private OrgRoleActionType getAction() {
        return enabled ? OrgRoleActionType.REVOKE : OrgRoleActionType.GRANT;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    public String getOrgDomain() {
        return orgDomain;
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
            user = app.storage().user().retrieve(email);
        }
        return user;
    }

    public Org getOrg() throws StorageException {
        if (org == null && orgDomain != null) {
            org = app.storage().org().retrieve(orgDomain);
        }
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(orgDomain, email, roleType);
    }    
}

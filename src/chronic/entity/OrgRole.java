/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.OrgKeyed;
import chronic.entitykey.PersonKeyed;
import chronic.entitykey.PersonKey;
import chronic.entitykey.OrgKey;
import chronic.entitytype.OrgRoleType;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.OrgRoleKeyed;
import chronic.entitykey.OrgPersonKey;
import chronic.entitykey.OrgPersonKeyed;
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgRoleTypeKeyed;
import chronic.entitykey.PersonRoleTypeKey;
import chronic.entitykey.PersonRoleTypeKeyed;
import chronic.entitytype.OrgRoleActionType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.AutoIdEntity;
import vellum.storage.StorageException;
import vellum.type.Enabled;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
@Entity()
@Table(name = "org_role")
public class OrgRole extends AutoIdEntity implements PersonKeyed, PersonRoleTypeKeyed,
        OrgKeyed, OrgPersonKeyed, OrgRoleKeyed, OrgRoleTypeKeyed, Enabled, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "org_role_id")
    Long id;
    
    @Column(name = "org_domain")
    String orgDomain;
    
    @Column()
    String email;
    
    @Column()
    OrgRoleType roleType;
    
    @Column()    
    boolean enabled = false;
    
    @OneToOne()    
    @JoinColumn(name = "org_domain", referencedColumnName = "org_domain", 
            insertable = false, updatable = false)
    Org org;
    
    @OneToOne()    
    @JoinColumn(name = "email", referencedColumnName = "email",
            insertable = false, updatable = false)
    Person person; 

    public OrgRole() {
    }
    
    public OrgRole(String orgDomain, String email) {
        this.orgDomain = orgDomain;
        this.email = email;
    }
    
    public OrgRole(Org org, Person user, OrgRoleType roleType) {
        this.org = org;
        this.person = user;
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
    public Long getId() {
        return id;
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
    public PersonKey getPersonKey() {
        return new PersonKey(email);
    }

    @Override
    public OrgPersonKey getOrgUserKey() {
        return new OrgPersonKey(orgDomain, email);
    }

    @Override
    public OrgRoleTypeKey getOrgRoleTypeKey() {
        return new OrgRoleTypeKey(orgDomain, roleType);
    }

    @Override
    public PersonRoleTypeKey getPersonRoleTypeKey() {
        return new PersonRoleTypeKey(email, roleType);
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

    public void setRoleType(OrgRoleType roleType) {
        this.roleType = roleType;
    }
    
    public OrgRoleType getRoleType() {
        return roleType;
    }
    
    public Person getPerson() {
        return person;
    }

    public Org getOrg() {
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(orgDomain, email, roleType);
    }    
}

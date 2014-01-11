/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitytype.OrgRoleType;
import chronic.entitykey.OrgRoleKey;
import chronic.entitytype.OrgRoleActionType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import vellum.entity.ComparableEntity;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.StorageException;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
@Entity()
@Table(name = "org_role")
public class OrgRole extends ComparableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "org_role_id")
    Long id;
    
    @Column(name = "org_domain")
    String orgDomain;
    
    @Column(length = 64)
    String email;
    
    @Column(name = "role_type", length = 32)
    @Enumerated(EnumType.STRING)
    OrgRoleType roleType = OrgRoleType.ADMIN;
    
    @Column()    
    boolean enabled = false;
    
    @ManyToOne()    
    @JoinColumn(name = "org_domain", referencedColumnName = "org_domain", insertable = false, updatable = false)
    Org org;
    
    @ManyToOne()    
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    Person person; 

    
    public OrgRole() {
    }
    
    public OrgRole(String orgDomain, String email, OrgRoleType roleType) {
        this.orgDomain = orgDomain;
        this.email = email;
        this.roleType = roleType;
    }
    
    public OrgRole(Org org, Person person, OrgRoleType roleType) {
        this.org = org;
        this.person = person;
        this.roleType = roleType;
        orgDomain = org.getOrgDomain();
        email = person.getEmail();
    }

    public OrgRole(OrgRoleKey key) {
        this.orgDomain = key.getOrgDomain();
        this.email = key.getEmail();
        this.roleType = key.getRoleType();
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    public void setPerson(Person person) {
        this.person = person;
    }
    
    public Person getPerson() {
        return person;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
    
    public Org getOrg() {
        return org;
    }    

    @Override
    public String toString() {
        return Args.format(orgDomain, email, roleType);
    }    
}

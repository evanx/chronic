/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import static chronic.entity.Cert_.id;
import chronic.entitykey.PersonKeyed;
import chronic.entitykey.PersonKey;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import vellum.data.Emails;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.storage.VellumEntity;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
@Entity
public class Person extends VellumEntity implements PersonKeyed, Enabled, Serializable {

    @Id
    @Column()
    String email;
    
    @Column()    
    String label;
    
    @Column(name = "login_time")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date loginTime;

    @Column(name = "logout_time")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date logoutTime;
    
    @Column()    
    boolean enabled = false;

    public Person() {
    }

    public Person(PersonKey key) {
        this(key.getEmail());
    }
    
    public Person(String email) {
        this.email = email;
        this.label = Emails.getUsername(email);
    }
    
    public Person(JMap map) throws JMapException {
        email = map.getString("email");
        label = map.getString("name");
    }
    
    @Override
    public Comparable getId() {
        return email;
    }

    @Override
    public PersonKey getPersonKey() {
        return new PersonKey(email);
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
    
    public Date getLoginTime() {
        return loginTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
        
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("email", email);
        map.put("label", label);
        map.put("enabled", enabled);
        return map;
    }

    @Override
    public String toString() {
        return getMap().toJson();
    }
}

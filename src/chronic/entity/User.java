/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.UserKeyed;
import chronic.entitykey.UserKey;
import java.util.Date;
import vellum.data.Emails;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.storage.AbstractEntity;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public class User extends AbstractEntity implements UserKeyed, Enabled {

    Long id;
    String email;
    String label;
    String firstName;
    String lastName;
    Date loginTime;
    Date logoutTime;
    boolean enabled = false;

    public User() {
    }

    public User(UserKey key) {
        this(key.getEmail());
    }
    
    public User(String email) {
        this.email = email;
        this.label = Emails.getUsername(email);
    }
    
    public User(JMap map) throws JMapException {
        email = map.getString("email");
        label = map.getString("name");
    }
    
    @Override
    public Comparable getKey() {
        return getUserKey();
    }

    @Override
    public UserKey getUserKey() {
        return new UserKey(email);
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
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

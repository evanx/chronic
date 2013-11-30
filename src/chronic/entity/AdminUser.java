/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import java.util.Date;
import vellum.parameter.StringMap;
import vellum.storage.AbstractEntity;

/**
 *
 * @author evan.summers
 */
public class AdminUser extends AbstractEntity {
    Long id;
    String email;
    String label;
    String firstName;
    String lastName;
    Date loginTime;
    Date logoutTime;
    boolean enabled = true;
    
    public AdminUser() {
    }

    @Override
    public Long getKey() {
        return id;
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
   
    public StringMap getStringMap() {
        StringMap map = new StringMap();
        map.put("id", id);
        map.put("email", email);
        map.put("label", label);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public String toString() {
        return getStringMap().toJson();
    }    
}

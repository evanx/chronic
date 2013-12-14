/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.util.ComparableValue;

/**
 *
 * @author evan.summers
 */
public class UserKey extends ComparableValue {
    String email;

    public UserKey(String email) {
        super(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
        
}

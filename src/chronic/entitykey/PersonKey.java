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
public class PersonKey extends ComparableValue {
    String email;

    public PersonKey(String email) {
        super(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
        
}

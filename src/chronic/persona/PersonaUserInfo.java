/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.persona;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.parameter.StringMap;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class PersonaUserInfo {
    static Logr logger = LogrFactory.getLogger(PersonaUserInfo.class);
    String email;
    String issuer;
    long expires;
    
    public PersonaUserInfo(StringMap map) {
        email = map.get("email");
        expires = map.getLong("expires");
        issuer = map.get("issuer");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return Args.format(email, issuer, expires);
    }
}

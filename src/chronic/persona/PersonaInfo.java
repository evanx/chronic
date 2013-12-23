/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.persona;

import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class PersonaInfo {
    static Logr logger = LogrFactory.getLogger(PersonaInfo.class);
    String email;
    String issuer;
    long expires;

    public PersonaInfo(String email) {
        this.email = email;
    }
    
    public PersonaInfo(JMap map) throws JMapException {
        email = map.getString("email");
        expires = map.getLong("expires");
        issuer = map.getString("issuer");
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

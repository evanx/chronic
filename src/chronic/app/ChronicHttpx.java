/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.persona.PersonaException;
import chronic.persona.PersonaInfo;
import chronic.persona.PersonaVerifier;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpx extends Httpx {

    Logger logger = LoggerFactory.getLogger(ChronicHttpx.class);

    public ChronicApp app;

    public ChronicHttpx(ChronicApp app, HttpExchange delegate) {
        super(delegate);
        this.app = app;
    }

    public String getEmail() throws JMapException, IOException, PersonaException {
        if (ChronicCookie.matches(getCookieMap())) {
            ChronicCookie cookie = new ChronicCookie(getCookieMap());
            if (cookie.getEmail() != null) {
                if (app.properties.isTesting()) {
                    if (getReferer().endsWith("/mimic")
                            && app.properties.getMimicEmail() != null
                            && app.properties.isAdmin(cookie.getEmail())) {
                        return app.properties.getMimicEmail();
                    } else {
                        return cookie.getEmail();
                    }
                }
                PersonaInfo personInfo = new PersonaVerifier(app, cookie).
                        getPersonaInfo(getHostUrl(), cookie.getAssertion());
                if (cookie.getEmail().equals(personInfo.getEmail())) {
                    return personInfo.getEmail();
                }
            }
        }
        logger.warn("getEmail cookie {}", getCookieMap());
        setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
        throw new PersonaException("no verified email");
    }
}

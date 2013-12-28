/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.persona.PersonaException;
import chronic.persona.PersonaInfo;
import chronic.persona.PersonaVerifier;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpx extends Httpx {

    static Logger logger = LoggerFactory.getLogger(ChronicHttpx.class);

    ChronicApp app;
    ChronicCookie cookie;
    
    public ChronicHttpx(ChronicApp app, HttpExchange delegate) {
        super(delegate);
        this.app = app;
    }

    public ChronicCookie getCookie() throws JMapException {
        if (cookie == null && ChronicCookie.matches(getCookieMap())) {
           cookie = new ChronicCookie(getCookieMap());
        }
        return cookie;
    }
    
    public String getEmail() throws JMapException, IOException, PersonaException {
        if (getCookie() != null) {
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

    public TimeZone getTimeZone() throws JMapException {
        if (getCookie() != null) {
            return getTimeZone(cookie.getTimezoneOffset());
        }
        return TimeZone.getDefault();
    }
            
    public static TimeZone getTimeZone(int timeZoneOffset) {
        String[] timeZoneIds = TimeZone.getAvailableIDs(timeZoneOffset);
        logger.info("timeZoneIds {}", timeZoneIds);
        if (timeZoneIds.length > 0) {
            String timeZoneId = timeZoneIds[0];
            return TimeZone.getTimeZone(timeZoneId);
        }        
        return TimeZone.getTimeZone(String.format("GMT%+03d", timeZoneOffset));
    }
    
    
}

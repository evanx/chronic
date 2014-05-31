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
import vellum.jx.JMapsException;

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

    public ChronicCookie getCookie() throws JMapsException {
        if (cookie == null) {
            if (ChronicCookie.matches(getCookieMap())) {
                cookie = new ChronicCookie(getCookieMap());
            } else {
                logger.warn("cookie not matching");
                return null;
            }
        }
        return cookie;
    }
    
    public String getEmail() throws JMapsException, IOException, PersonaException {
        if (getCookie() != null) {
            if (cookie.getEmail() != null) {
                if (app.properties.isTesting()) {
                    if (getReferer() != null && getReferer().endsWith("/mimic")
                            && app.properties.getMimicEmail() != null
                            && app.properties.isAdmin(cookie.getEmail())) {
                        return app.properties.getMimicEmail();
                    } else {
                        return cookie.getEmail();
                    }
                }
                PersonaInfo personInfo = new PersonaVerifier(app, cookie).
                        getPersonaInfo(getHostUrl(), cookie.getAssertion());
                if (!cookie.getEmail().equals(personInfo.getEmail())) {
                    logger.warn("email differs: persona {}, cookie {}", personInfo.getEmail(), cookie.getEmail());
                } else {
                    return personInfo.getEmail();
                }
            }
        }
        logger.warn("getEmail cookie {}", getCookieMap());
        setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
        throw new PersonaException("no verified email");
    }

    public TimeZone getTimeZone() throws JMapsException {
        if (getCookie() != null) {
            return getTimeZone(cookie.getTimeZoneOffset());
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

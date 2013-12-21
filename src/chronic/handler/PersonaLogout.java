/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.User;
import chronic.app.ChronicCookie;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class PersonaLogout implements ChronicHttpxHandler {

    static Logger logger = LoggerFactory.getLogger(PersonaLogout.class);
    ChronicCookie cookie;
    
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        logger.info("handle", getClass().getSimpleName(), httpx.getPath());
        try {
            String email = httpx.parseJsonMap().getString("email");
            if (ChronicCookie.matches(httpx.getCookieMap())) {
                cookie = new ChronicCookie(httpx.getCookieMap());
                logger.debug("cookie {}", cookie.getEmail());
                if (!cookie.getEmail().equals(email)) {
                    logger.warn("email {}", email);
                }
                httpx.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                if (httpx.app.getProperties().isTesting()) {
                    logger.info("testing mode: ignoring logout");
                } else {
                    logger.info("cookie", cookie.getEmail());
                    User user = httpx.db.user().retrieve(cookie.getEmail());
                    user.setLogoutTime(new Date());
                    httpx.db.user().replace(user);
                }
            }
            return new JMap();
        } finally {
            httpx.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
        }
    }
}

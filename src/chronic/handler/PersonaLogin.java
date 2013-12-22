/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.User;
import chronic.app.ChronicCookie;
import chronic.persona.PersonaUserInfo;
import chronic.persona.PersonaVerifier;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class PersonaLogin implements ChronicHttpxHandler {

    static Logger logger = LoggerFactory.getLogger(PersonaLogin.class);
    String assertion;
    String timezoneOffset;
    ChronicCookie cookie;
    
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        JMap map = httpx.parseJsonMap();
        timezoneOffset = map.getString("timezoneOffset");
        logger.trace("timezoneOffset {}", timezoneOffset);
        assertion = map.getString("assertion");
        if (ChronicCookie.matches(httpx.getCookieMap())) {
            cookie = new ChronicCookie(httpx.getCookieMap());
        }
        PersonaUserInfo userInfo = new PersonaVerifier(httpx.app, cookie).getUserInfo(
                httpx.getHostUrl(), assertion);
        logger.trace("persona {}", userInfo);
        String email = userInfo.getEmail();
        User user = httpx.db.user().find(email);
        if (user == null) {
            user = new User(email);
            user.setEnabled(true);
            user.setLoginTime(new Date());
            httpx.db.user().persist(user);
            logger.info("insert user {}", email);
        } else {
            user.setEnabled(true);
            user.setLoginTime(new Date());
            httpx.db.user().update(user);
        }
        cookie = new ChronicCookie(user.getEmail(), user.getLabel(),
                user.getLoginTime().getTime(), assertion);
        JMap cookieMap = cookie.toMap();
        logger.trace("cookie {}", cookieMap);
        httpx.setCookie(cookieMap, ChronicCookie.MAX_AGE_MILLIS);
        return cookieMap;
    }
}

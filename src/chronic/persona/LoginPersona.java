/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.persona;

import chronic.ChronicApp;
import chronic.entity.User;
import chronic.ChronicCookie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class LoginPersona implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    Httpx httpx;
    String assertion;
    String timezoneOffset;
    
    public LoginPersona(ChronicApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpx = new Httpx(httpExchange);
        try {
            JMap map = httpx.parseJsonMap();
            timezoneOffset = map.getString("timezoneOffset");
            logger.trace("timezoneOffset {}", timezoneOffset);
            assertion = map.getString("assertion");
            handle();
        } catch (Exception e) {
            httpx.handleError(e);
        }
        httpExchange.close();
    }

    ChronicCookie cookie;
    
    private void handle() throws Exception {
        logger.info("address {}", httpx.getServerUrl());
        if (ChronicCookie.matches(httpx.getCookieMap())) {
            cookie = new ChronicCookie(httpx.getCookieMap());
        }            
        PersonaUserInfo userInfo = new PersonaVerifier(app, cookie).getUserInfo(
                httpx.getServerUrl(), assertion);
        logger.trace("persona {}", userInfo);
        String email = userInfo.getEmail();
        User user = app.getStorage().users().select(email);
        if (user == null) {
            user = new User(email);
            user.setEnabled(true);
            user.setLoginTime(new Date());
            app.getStorage().users().insert(user);
            logger.info("insert user {}", email);
        } else {
            user.setEnabled(true);
            user.setLoginTime(new Date());
            app.getStorage().users().update(user);
        }
        cookie = new ChronicCookie(user.getEmail(), user.getLabel(), 
                user.getLoginTime().getTime(), assertion);
        JMap cookieMap = cookie.toMap();
        logger.trace("cookie {}", cookieMap);
        httpx.setCookie(cookieMap, ChronicCookie.MAX_AGE_MILLIS);
        httpx.sendResponse(cookieMap);
    }
}

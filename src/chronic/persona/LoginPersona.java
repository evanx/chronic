/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.persona;

import chronic.ChronicApp;
import chronic.entity.AdminUser;
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

    public LoginPersona(ChronicApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpx = new Httpx(httpExchange);
        try {
            assertion = httpx.parseJsonMap().getString("assertion");
            if (assertion != null) {
                handle();
            } else {
                httpx.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                httpx.handleError("missing assertion");
            }
        } catch (Exception e) {
            httpx.handleError(e);
        }
        httpExchange.close();
    }

    AdminUser adminUser;
    
    private void handle() throws Exception {
        logger.info("address {}", httpx.getServerUrl());
        PersonaUserInfo userInfo = PersonaVerifier.getUserInfo(
                httpx.getServerUrl(), 
                assertion);
        String email = userInfo.getEmail();
        if (app.getStorage().getAdminUserStorage().containsKey(email)) {
            adminUser = app.getStorage().getAdminUserStorage().select(email);
        } else {
            adminUser = new AdminUser(email);
            app.getStorage().getAdminUserStorage().insert(adminUser);
            logger.info("new email {}", email);
        }
        adminUser.setEnabled(true);
        adminUser.setLoginTime(new Date());
        app.getStorage().getAdminUserStorage().update(adminUser);
        handle(adminUser.getEmail(), adminUser.getLabel(), adminUser.getLoginTime().getTime());
    }

    private void handle(String email, String label, long loginTime) throws Exception {
        ChronicCookie cookie = new ChronicCookie(email, label, loginTime, assertion);
        JMap map = cookie.toMap();
        httpx.setCookie(map, ChronicCookie.MAX_AGE_MILLIS);
        map.put("admin", app.isAdmin(email));
        httpx.sendResponse(map);
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.persona;

import chronic.ChronicApp;
import chronic.entity.AdminUser;
import chronic.ChronicCookie;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Emails;

/**
 *
 * @author evan.summers
 */
public class LoginPersona implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    Httpx httpExchangeInfo;
    String assertion;

    public LoginPersona(ChronicApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchangeInfo = new Httpx(httpExchange);
        try {
            assertion = httpExchangeInfo.parseJsonMap().getString("assertion");
            if (assertion != null) {
                if (app.getProperties().isTesting()
                        && app.getProperties().getAdminEmail() != null) {
                    handleAdmin(app.getProperties().getAdminEmail());
                } else {
                    handle();
                }
            } else {
                httpExchangeInfo.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                httpExchangeInfo.handleError("missing assertion");
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    AdminUser adminUser;
    
    private void handle() throws Exception {
        PersonaUserInfo userInfo = app.getPersonaVerifier().getUserInfo(assertion);
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

    private void handleAdmin(String email) throws Exception {
        handle(email, Emails.getUsername(email), System.currentTimeMillis());
    }

    private void handle(String email, String label, long loginTime) throws Exception {
        ChronicCookie cookie = new ChronicCookie(email, label, loginTime, assertion);
        httpExchangeInfo.setCookie(cookie.toMap(), ChronicCookie.MAX_AGE_MILLIS);
        httpExchangeInfo.sendResponse("text/json", true);
        String json = new Gson().toJson(cookie.toMap());
        httpExchangeInfo.getPrintStream().println(json);
    }
}

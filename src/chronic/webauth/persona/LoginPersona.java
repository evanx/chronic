/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.webauth.persona;

import chronic.ChronicApp;
import chronic.entity.AdminUser;
import chronic.webauth.ChronicCookie;
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
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;    
    String assertion;

    public LoginPersona(ChronicApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        assertion = httpExchangeInfo.parseJsonMap().get("assertion");
        try {
            if (assertion != null) {
                if (app.getProperties().isTesting() && 
                        app.getProperties().getAdminEmail() != null) {
                    handleAdmin(app.getProperties().getAdminEmail());
                } else {
                    handle(app.getPersonaVerifier().getUserInfo(assertion));
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

    private void handle(PersonaUserInfo userInfo) throws Exception {
        AdminUser user = app.getStorage().getAdminUserStorage().select(userInfo.getEmail());
        user.setEnabled(true);
        user.setLoginTime(new Date());
        app.getStorage().getAdminUserStorage().update(user);
        handle(user.getEmail(), user.getLabel(), user.getLoginTime().getTime());
    }

    private void handleAdmin(String adminEmail) throws Exception {
        handle(adminEmail, Emails.getUsername(adminEmail), System.currentTimeMillis());
    }
    
    private void handle(String email, String label, long loginTime) throws Exception {
        ChronicCookie cookie = new ChronicCookie(email, label, loginTime, assertion); 
        httpExchangeInfo.setCookie(cookie.toMap(), ChronicCookie.MAX_AGE_MILLIS);
        httpExchangeInfo.sendResponse("text/json", true);
        String json = new Gson().toJson(cookie.toMap());
        httpExchangeInfo.getPrintStream().println(json);
    }
}

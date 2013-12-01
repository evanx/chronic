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
import vellum.parameter.StringMap;

/**
 *
 * @author evan.summers
 */
public class LoginPersona implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PersonaUserInfo userInfo;

    public LoginPersona(ChronicApp app) {
        super();
        this.app = app;
    }
    
    String assertion;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.trace("handle {} {}", httpExchangeInfo.getPath(), httpExchangeInfo.getParameterMap());
        assertion = httpExchangeInfo.getParameter("assertion");
        logger.trace("handle assertion length {}", assertion.length());
        try {
            if (assertion != null) {
                if (app.getProperties().isTesting() && 
                        app.getProperties().getAdminEmail() != null) {
                    handleAdmin(app.getProperties().getAdminEmail());
                } else {
                    handle();
                }
            } else {
                httpExchangeInfo.handleError("missing assertion");
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
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
        ChronicCookie cookie = new ChronicCookie(email, label, loginTime); 
        httpExchangeInfo.setCookie(cookie.toMap(), ChronicCookie.MAX_AGE_MILLIS);
        httpExchangeInfo.sendResponse("text/json", true);
        StringMap responseMap = new StringMap();
        responseMap.put("email", email);
        responseMap.put("label", label);
        responseMap.put("authCode", cookie.getAuthCode());
        String json = new Gson().toJson(responseMap);
        logger.info(json);
        httpExchangeInfo.getPrintStream().println(json);
    }
    
}

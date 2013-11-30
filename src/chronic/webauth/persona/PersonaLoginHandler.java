/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.webauth.persona;

import chronic.ChronicApp;
import chronic.entity.AdminUser;
import chronic.webauth.ChronicCookie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.util.JsonStrings;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.util.Date;
import vellum.datatype.Emails;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.parameter.StringMap;

/**
 *
 * @author evan.summers
 */
public class PersonaLoginHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    ChronicApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    public PersonaLoginHandler(ChronicApp app) {
        super();
        this.app = app;
    }
    
    String userId;
    String assertion;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), 
                httpExchangeInfo.getPath(), httpExchangeInfo.getParameterMap());
            assertion = httpExchangeInfo.getParameter("assertion");
            logger.info("input", userId, assertion);
            try {
                if (assertion != null) {
                    handle();
                } else {
                    httpExchangeInfo.handleError("require assertion");
                }
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        httpExchange.close();
    }
    
    PersonaUserInfo userInfo;
    
    private void handle() throws Exception {
        userInfo = new PersonaApi(app.getProperties().getServerUrl()).getUserInfo(assertion);
        logger.info("userInfo", userInfo);
        AdminUser user = app.getStorage().getAdminUserStorage().select(userInfo.getEmail());
        if (user == null) {
            user = new AdminUser();
            user.setEmail(userInfo.getEmail());
            user.setFirstName(Emails.getUsername(userInfo.getEmail()));
            user.setLabel(Emails.getUsername(userInfo.getEmail()));
            user.setEnabled(true);
            user.setLoginTime(new Date());
            app.getStorage().getAdminUserStorage().update(user);
        } else {
            app.getStorage().getAdminUserStorage().insert(user);
        }
        ChronicCookie cookie = new ChronicCookie(user.getEmail(), user.getLabel(), 
                user.getLoginTime().getTime(), assertion);
        httpExchangeInfo.setCookie(cookie.toMap(), ChronicCookie.MAX_AGE_MILLIS);
        httpExchangeInfo.sendResponse("text/json", true);
        StringMap responseMap = new StringMap();
        responseMap.put("email", user.getEmail());
        responseMap.put("label", user.getLabel());
        responseMap.put("authCode", cookie.getAuthCode());
        String json = JsonStrings.buildJson(responseMap);
        logger.info(json);
        httpExchangeInfo.getPrintStream().println(json);
    }
}

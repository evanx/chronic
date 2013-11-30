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
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class PersonaLogoutHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    ChronicApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    ChronicCookie cookie;

    public PersonaLogoutHandler(ChronicApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath());
        try {
            cookie = new ChronicCookie(httpExchangeInfo.getCookieMap());
            if (cookie.getEmail() == null) {
                logger.warn("cookie", cookie);
                if (false) {
                    httpExchangeInfo.handleError("No username in cookie");
                }
            } else {
                handle();
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        logger.info("cookie", cookie);
        AdminUser user = app.getStorage().getAdminUserStorage().select(cookie.getEmail());
        user.setLogoutTime(new Date());
        app.getStorage().getAdminUserStorage().update(user);
        httpExchangeInfo.clearCookie(ChronicCookie.names());
        httpExchangeInfo.sendResponse("text/json", true);
        String json = JsonStrings.buildJson(cookie.toMap());
        logger.info("json", json);
        httpExchangeInfo.getPrintStream().print(json);
    }
}

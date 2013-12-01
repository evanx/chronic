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
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class LogoutPersona implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    ChronicCookie cookie;

    public LogoutPersona(ChronicApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath());
        try {
            if (ChronicCookie.matches(httpExchangeInfo.getCookieMap())) {
                cookie = new ChronicCookie(httpExchangeInfo.getCookieMap());
                httpExchangeInfo.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                logger.debug("cookie {}", cookie.getEmail());
                if (app.getProperties().isTesting()) {
                    logger.info("testing mode: ignoring logout");
                    httpExchangeInfo.sendEmptyOkResponse();
                } else {
                    handle();
                }
            } else {
                httpExchangeInfo.sendEmptyOkResponse();
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        logger.info("cookie", cookie.getEmail());
        AdminUser user = app.getStorage().getAdminUserStorage().select(cookie.getEmail());
        user.setLogoutTime(new Date());
        app.getStorage().getAdminUserStorage().update(user);
        httpExchangeInfo.sendEmptyOkResponse();
    } 
}

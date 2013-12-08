/*
 * Source https://code.google.com/p/vellum by @evanxsummers
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

/**
 *
 * @author evan.summers
 */
public class LogoutPersona implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    Httpx httpExchangeInfo;
    ChronicCookie cookie;

    public LogoutPersona(ChronicApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath());
        try {
            String email = httpExchangeInfo.parseJsonMap().getString("email");
            if (ChronicCookie.matches(httpExchangeInfo.getCookieMap())) {
                cookie = new ChronicCookie(httpExchangeInfo.getCookieMap());
                logger.debug("cookie {}", cookie.getEmail());
                if (!cookie.getEmail().equals(email)) {
                    logger.warn("email {}", email);
                }
                httpExchangeInfo.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                if (app.getProperties().isTesting()) {
                    logger.info("testing mode: ignoring logout");
                    httpExchangeInfo.sendEmptyOkResponse();
                } else {
                    handle();
                }
            } else {
                httpExchangeInfo.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
                httpExchangeInfo.sendEmptyOkResponse();
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        logger.info("cookie", cookie.getEmail());
        User user = app.getStorage().getUserStorage().find(cookie.getEmail());
        user.setLogoutTime(new Date());
        app.getStorage().getUserStorage().update(user);
        httpExchangeInfo.sendEmptyOkResponse();
    } 
}

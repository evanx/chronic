/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.webauth.ChronicCookie;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class GetStatus {
    
    Logger logger = LoggerFactory.getLogger(GetStatus.class);
    ChronicApp app;
    
    public GetStatus(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        Httpx httpExchangeInfo = new Httpx(httpExchange);
        try {
            if (ChronicCookie.matches(httpExchangeInfo.getCookieMap())) {
                logger.trace("cookieMap {}", httpExchangeInfo.getCookieMap());
                ChronicCookie cookie = new ChronicCookie(httpExchangeInfo.getCookieMap());
                logger.debug("cookie {}", cookie.getEmail());
                if (app.getProperties().isTesting()) {
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
    }
    
}

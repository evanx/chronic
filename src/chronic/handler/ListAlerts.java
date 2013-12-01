/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.webauth.ChronicCookie;
import chronic.webauth.persona.PersonaUserInfo;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class ListAlerts {

    Logger logger = LoggerFactory.getLogger(ListAlerts.class);
    ChronicApp app;
    Httpx httpx;

    public ListAlerts(ChronicApp app) {
        this.app = app;
    }

    public void handle(HttpExchange httpExchange) throws Exception {
        httpx = new Httpx(httpExchange);
        try {
            if (ChronicCookie.matches(httpx.getCookieMap())) {
                logger.trace("cookieMap {}", httpx.getCookieMap());
                ChronicCookie cookie = new ChronicCookie(httpx.getCookieMap());
                logger.debug("cookie {}", cookie.getEmail());
                if (!app.getProperties().isTesting()) {
                    PersonaUserInfo userInfo =
                            app.getPersonaVerifier().getUserInfo(cookie.getAccessToken());
                    logger.debug("userInfo {}", userInfo);
                }
                handle();
            } else {
                httpx.sendEmptyOkResponse();
            }
        } catch (Exception e) {
            httpx.handleError(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        List statusList = new ArrayList();
        for (StatusRecord status : app.getAlertList()) {
            statusList.add(status);
        }
        logger.info("map", JMaps.create("alertList", statusList));
        httpx.sendResponse(JMaps.create("alertList", statusList));
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.util.StatusRecordDescendingTimestampComparator;
import chronic.ChronicCookie;
import chronic.persona.PersonaUserInfo;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
    PersonaUserInfo userInfo;

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
                userInfo = app.getPersonaVerifier().getUserInfo(cookie.getAccessToken());
                logger.debug("userInfo {}", userInfo);
                if (userInfo == null) {
                    logger.warn("user not verified {}", cookie.getEmail());
                } else if (!cookie.getEmail().equals(userInfo.getEmail())) {
                    logger.warn("user not consistent with cookie {}", userInfo.getEmail());
                } else {
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
        List alertList = new LinkedList();
        for (StatusRecord status : descendingTimestamp(app.getAlertList())) {
            if (status.getService() != null) {
                if (userInfo != null && userInfo.getEmail().endsWith(status.getOrgName())) {
                    alertList.add(status.getAlertMap());
                } else if (userInfo.getEmail().equals(app.getProperties().getAdminEmail()))  {
                    alertList.add(status.getAlertMap());
                } else {
                    alertList.add(status.getAlertMap());
                }
            }
        }
        httpx.sendResponse(JMaps.create("alertList", alertList));
    }

    public static Iterable<StatusRecord> descendingTimestamp(Collection<StatusRecord> list) {
        LinkedList sortedList = new LinkedList(list);
        Collections.sort(sortedList, new StatusRecordDescendingTimestampComparator());
        return sortedList;
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.ChronicCookie;
import chronic.persona.PersonaUserInfo;
import chronic.persona.PersonaVerifier;
import chronic.util.StatusRecordDescendingTimestampComparator;
import com.sun.net.httpserver.HttpExchange;
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
    String email;
    boolean admin;
    
    public ListAlerts(ChronicApp app) {
        this.app = app;
    }

    public void handle(HttpExchange httpExchange) throws Exception {
        httpx = new Httpx(httpExchange);
        try {
            if (ChronicCookie.matches(httpx.getCookieMap())) {
                logger.trace("cookieMap {}", httpx.getCookieMap());
                ChronicCookie cookie = new ChronicCookie(httpx.getCookieMap());
                logger.debug("cookie email {}", cookie.getEmail());
                userInfo = new PersonaVerifier(app, cookie).getUserInfo(httpx.getServerUrl(), 
                        cookie.getAccessToken());
                logger.info("persona {}", userInfo);
                if (cookie.getEmail() == null) {
                    httpx.handleError("empty cookie");
                } else if (userInfo == null) {
                    httpx.handleError("user not verified");
                } else if (userInfo.getEmail() == null) {
                    httpx.handleError("no email");
                } else if (!cookie.getEmail().equals(userInfo.getEmail())) {
                    httpx.handleError("invalid cookie");
                } else {
                    email = userInfo.getEmail();
                    admin = app.isAdmin(email);
                    handle();
                }
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
                alertList.add(status.getAlertMap(admin));
            } else {
            logger.warn("exclude {} {}", status.getOrgName(), email);                
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

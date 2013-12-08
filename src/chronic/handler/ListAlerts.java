/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.util.AlertRecordDescendingTimestampComparator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class ListAlerts implements ChronicHandler {

    Logger logger = LoggerFactory.getLogger(ListAlerts.class);
    
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getVerifiedEmail(httpx);
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                new AlertRecordDescendingTimestampComparator())) {
            if (alert.getStatus().getService() != null) {
                alerts.add(alert.getAlertMap(app.getProperties().isAdmin(email)));
            } else {
                logger.warn("exclude {} {}", alert.getStatus().getOrgUrl(), email);
            }
        }
        return JMaps.create("alerts", alerts);
    }
}

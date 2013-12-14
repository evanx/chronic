/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class ListAlerts implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(ListAlerts.class);
    
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                TimestampedComparator.reverse())) {
            if (app.store().isSubscriber(email, alert)) {
                alerts.add(alert.getAlertMap(true));
            } else if (app.getProperties().isAdmin(email)) {
                alerts.add(alert.getAlertMap(true));
            }
        }
        return JMaps.create("alerts", alerts);
    }
}

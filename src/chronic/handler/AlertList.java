/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.AlertRecord;
import chronic.entitykey.SubscriberKey;
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
public class AlertList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(AlertList.class);
    
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                TimestampedComparator.reverse())) {
            if (app.storage().sub().containsKey(
                    new SubscriberKey(alert.getStatus().getTopic().getId(), email))) {
                alerts.add(alert.getlMap());
            } else if (app.getProperties().isAdmin(email)) {
                alerts.add(alert.getlMap());
            } else if (app.getProperties().isDemo(httpx)) {
                alerts.add(alert.getPartialMap());
            }
        }
        return JMaps.mapValue("alerts", alerts);
    }
}

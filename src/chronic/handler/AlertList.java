/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.AlertRecord;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entitykey.SubscriberKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(httpx.app.getAlertMap().values(),
                TimestampedComparator.reverse())) {            
            if (es.isSubscription(alert.getStatus().getTopic(), email)) {
                alerts.add(alert.getMap());
            } else if (httpx.app.getProperties().isAdmin(email)) {
                alerts.add(alert.getMap());
            } else if (httpx.getReferer().endsWith("/demo")) {
                alerts.add(alert.getPartialMap());
            }
        }
        return JMaps.mapValue("alerts", alerts);
    }
}

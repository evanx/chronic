/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.alert.AlertRecord;
import chronic.alert.AlertRecordMapper;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
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
        TimeZone timeZone = httpx.getTimeZone();
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                TimestampedComparator.reverse())) {            
            AlertRecordMapper mapper = new AlertRecordMapper(alert, timeZone);
            if (es.isSubscription(alert.getStatus().getTopic().getId(), email)) {
                alerts.add(mapper.getExtendedMap());
            } else if (httpx.getReferer().endsWith("/demo")) {
                alerts.add(mapper.getBasicMap());
            } else if (app.getProperties().isAdmin(email)) {
                alerts.add(mapper.getExtendedMap());
            }
        }
        return JMaps.mapValue("alerts", alerts);
    }
}

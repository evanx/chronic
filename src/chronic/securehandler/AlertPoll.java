/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.securehandler;

import chronic.app.ChronicHttpx;
import chronic.alert.AlertEvent;
import chronic.alert.TopicMessage;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.data.TimestampedComparator;
import vellum.format.CalendarFormats;
import vellum.util.Calendars;
import vellum.util.Lists;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AlertPoll implements ChronicPlainHttpxHandler {

    Logger logger = LoggerFactory.getLogger(AlertPoll.class);
    
    TimeZone timeZone;
    
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        String timeZoneId = httpx.readString();
        timeZone = Calendars.getTimeZone(timeZoneId);
        if (!cert.isEnabled()) {
            return "Cert not enabled\n";
        }
        for (AlertEvent alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                TimestampedComparator.reverse())) {
            if (!alert.isPolled() && alert.getStatus().getStatusType().isStatusKnown() && 
                    alert.getStatus().getAlertType().isPollable() &&
                    System.currentTimeMillis() - alert.getTimestamp() < Millis.fromMinutes(3)) {
                alert.setPolled(true);
                return buildPlain(alert);
            }
        }
        return "NONE\n";
    }
    
    public String buildPlain(AlertEvent alert) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Time: %s\n", CalendarFormats.timestampZoneFormat.format(timeZone, alert.getTimestamp())));
        builder.append(String.format("Topic: %s\n", alert.getStatus().getTopicLabel()));
        builder.append(String.format("Subject: %s\n", formatSubject(alert.getStatus())));
        builder.append("\n");
        for (String line : Strings.trimLines(alert.getStatus().getLineList())) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }

    public static String formatSubject(TopicMessage status) {
        return String.format("%s %s", status.getTopicLabel(), status.getStatusType());
    }
}
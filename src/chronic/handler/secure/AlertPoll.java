/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.secure;

import chronic.app.ChronicHttpx;
import chronic.alert.TopicEvent;
import chronic.alert.TopicMessage;
import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import java.util.Calendar;
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
public class AlertPoll implements PlainHttpxHandler {

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
        for (TopicEvent event : Lists.sortedLinkedList(app.getEventMap().values(),
                TimestampedComparator.reverse())) {
            if (event.getPolled() == null && event.getMessage().getStatusType().isStatusKnown() && 
                    event.getMessage().getAlertType().isPollable() &&
                    System.currentTimeMillis() - event.getTimestamp() < Millis.fromMinutes(3)) {
                event.setPolled(Calendar.getInstance(timeZone));
                return buildPlain(event);
            }
        }
        return "NONE\n";
    }
    
    public String buildPlain(TopicEvent alert) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Time: %s\n", CalendarFormats.timestampZoneFormat.format(timeZone, alert.getTimestamp())));
        builder.append(String.format("Topic: %s\n", alert.getMessage().getTopicLabel()));
        builder.append(String.format("Subject: %s\n", formatSubject(alert.getMessage())));
        builder.append("\n");
        for (String line : Strings.trimLines(alert.getMessage().getLineList())) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }

    public static String formatSubject(TopicMessage status) {
        return String.format("%s %s", status.getTopicLabel(), status.getStatusType());
    }
}

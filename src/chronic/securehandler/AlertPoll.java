/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.securehandler;

import chronic.app.ChronicHttpx;
import chronic.alert.AlertRecord;
import chronic.alert.StatusRecord;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.data.TimestampedComparator;
import vellum.util.Lists;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AlertPoll implements ChronicPlainHttpxHandler {

    Logger logger = LoggerFactory.getLogger(AlertPoll.class);

    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        if (!cert.isEnabled()) {
            return "Cert not enabled\n";
        }
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
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
    
    public String buildPlain(AlertRecord alert) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Time: %s\n", Millis.formatTime(alert.getTimestamp())));
        builder.append(String.format("Topic: %s\n", alert.getStatus().getTopicLabel()));
        builder.append(String.format("Subject: %s\n", formatSubject(alert.getStatus())));
        builder.append("\n");
        for (String line : Strings.trimLines(alert.getStatus().getLineList())) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }

    public static String formatSubject(StatusRecord status) {
        return String.format("%s %s", status.getTopicLabel(), status.getStatusType());
    }
}

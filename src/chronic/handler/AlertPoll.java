/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.alert.AlertRecord;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
import vellum.util.Lists;

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
            return "Cert not enabled";
        }
        List alerts = new LinkedList();
        for (AlertRecord alert : Lists.sortedLinkedList(app.getAlertMap().values(),
                TimestampedComparator.reverse())) {
        }
        return "";
    }
}

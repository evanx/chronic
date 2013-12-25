/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Subscription;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class SubscriptionList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriptionList.class);
  
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        logger.info("email {}", email);
        List subscriptions = new LinkedList();
        for (Subscription subscriber : es.listSubscription(email)) {
            subscriptions.add(subscriber);
        }
        logger.info("subscriptions {}", subscriptions);
        List otherSubscriptions = new LinkedList();
        if (httpx.app.getProperties().isAdmin(email)) {
            for (Subscription subscriber : es.listSubcriber()) {
                logger.info("admin subscriber {}", subscriber);
                if (!subscriber.getPerson().getEmail().equals(email)) {
                    otherSubscriptions.add(subscriber);
                }
            }
        } else if (httpx.getReferer().endsWith("/demo")) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (Subscription subscriber : es.listSubscription(adminEmail)) {
                logger.info("demo subscriber {}", subscriber);
                subscriptions.add(subscriber);
            }
        }
        logger.info("subscribers {}", otherSubscriptions);
        return new JMap(JMaps.entry("subscriptions", subscriptions),
                JMaps.entry("otherSubscriptions", otherSubscriptions));
    }
    
}

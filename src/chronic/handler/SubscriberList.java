/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.entity.Subscriber;
import chronic.entitykey.PersonKey;
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
public class SubscriberList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberList.class);
  
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx) throws Exception {
        String email = httpx.getEmail();
        logger.info("email {}", email);
        List subscriptions = new LinkedList();
        for (Subscriber subscriber : httpx.db.sub().list(new PersonKey(email))) {
            subscriptions.add(subscriber);
        }
        logger.info("subscriptions {}", subscriptions);
        List subscribers = new LinkedList();
        if (httpx.app.getProperties().isAdmin(email)) {
            for (Subscriber subscriber : httpx.db.sub().list()) {
                logger.info("admin subscriber {}", subscriber);
                if (!subscriber.getEmail().equals(email)) {
                    subscribers.add(subscriber);
                }
            }
        } else if (httpx.getReferer().endsWith("/demo")) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (Subscriber subscriber : httpx.db.sub().list(new PersonKey(adminEmail))) {
                logger.info("demo subscriber {}", subscriber);
                subscriptions.add(subscriber);
            }
        }
        logger.info("subscribers {}", subscribers);
        httpx.injectDatabase(subscribers);
        httpx.injectDatabase(subscriptions);
        return new JMap(
                JMaps.entry("subscribers", subscribers),
                JMaps.entry("subscriptions", subscriptions));
    }
    
}

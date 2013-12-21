/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entitykey.UserKey;
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
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        List subscriptions = new LinkedList();
        for (Subscriber subscriber : httpx.db.sub().list(new UserKey(email))) {
            subscriptions.add(subscriber);
        }
        List subscribers = new LinkedList();
        if (httpx.app.getProperties().isAdmin(email)) {
            for (Subscriber subscriber : httpx.db.sub().list()) {
                if (!subscriber.getEmail().equals(email)) {
                    subscribers.add(subscriber);
                }
            }
        } else if (httpx.app.getProperties().isDemo(httpx)) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (Subscriber subscriber : httpx.db.sub().list(new UserKey(adminEmail))) {
                subscriptions.add(subscriber);
            }
        }
        httpx.injectDatabase(subscribers);
        httpx.injectDatabase(subscriptions);
        return new JMap(
                JMaps.entry("subscribers", subscribers),
                JMaps.entry("subscriptions", subscriptions));
    }
    
}

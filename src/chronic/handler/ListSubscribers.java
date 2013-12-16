/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entitykey.UserKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class ListSubscribers implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(ListSubscribers.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        List subscriptions = new LinkedList();
        for (Subscriber subscriber : app.storage().sub().list(new UserKey(email))) {
            subscriptions.add(subscriber.getMap());
        }
        List subscribers = new LinkedList();
        if (app.getProperties().isAdmin(email)) {
            for (Subscriber subscriber : app.storage().sub().list()) {
                if (!subscriber.getEmail().equals(email)) {
                    subscribers.add(subscriber.getMap());
                }
            }
        }
        return new JMap(JMaps.entry("subscribers", subscribers),
                JMaps.entry("subscriptions", subscriptions));
    }
    
}

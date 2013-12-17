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
public class SubscriberActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberActionNone.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        List subscribers = new LinkedList();        
        for (Subscriber subscriber : app.storage().sub().list(new UserKey(email))) {
            subscriber.setEnabled(false);
            app.storage().sub().update(subscriber);
            subscribers.add(subscriber.getMap());            
        }
        return JMaps.create("subscriptions", subscribers);
    }
    
}
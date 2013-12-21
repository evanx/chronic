/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
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
public class SubscriberActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberActionNone.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.getEmail();
        List subscribers = new LinkedList();        
        for (Subscriber subscriber : httpx.db.sub().list(new UserKey(email))) {
            subscriber.setEnabled(false);
            httpx.db.sub().replace(subscriber);
            subscribers.add(subscriber);
        }
        httpx.injectDatabase(subscribers);
        return JMaps.map("subscriptions", subscribers);
    }
    
}

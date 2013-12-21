/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Subscriber;
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
public class SubscriberActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberActionAll.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        List subscribers = new LinkedList();        
        for (Subscriber subscriber : httpx.db.sub().list(email)) {
            subscriber.setEnabled(true);
            httpx.db.sub().replace(subscriber);
            subscribers.add(subscriber);
        }
        return JMaps.map("subscriptions", subscribers);
    }
    
}

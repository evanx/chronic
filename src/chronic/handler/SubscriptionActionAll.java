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
public class SubscriptionActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriptionActionAll.class);
  
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        List subscriptions = new LinkedList();        
        for (Subscription subscription : es.listSubscription(email)) {
            subscription.setEnabled(true);
            subscriptions.add(subscription);
        }
        return JMaps.map("subscriptions", subscriptions);
    }
    
}

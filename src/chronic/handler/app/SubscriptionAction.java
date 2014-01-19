/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.app;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class SubscriptionAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriptionAction.class);
  
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        JMap map = httpx.parseJsonMap().getMap("subscription");
        Subscription subscription = es.findSubscription(map.getLong("topicId"), email);
        subscription.setEnabled(!subscription.isEnabled());
        return JMaps.mapValue("subscription", subscription.getMap());
    }
    
}

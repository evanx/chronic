/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entitykey.SubscriberKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class SubscriberAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberAction.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.getEmail();
        JMap map = httpx.parseJsonMap().getMap("subscriber");
        SubscriberKey key = new SubscriberKey(map.getLong("topicId"), email);
        Subscriber subscriber = httpx.db.sub().find(key);
        subscriber.setEnabled(!subscriber.isEnabled());
        httpx.db.sub().replace(subscriber);
        return JMaps.mapValue("subscriber", subscriber.getMap());
    }
    
}

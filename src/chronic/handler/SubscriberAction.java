/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entitykey.SubscriberKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class SubscriberAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscriberAction.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        JMap map = httpx.parseJsonMap().getMap("subscriber");
        SubscriberKey key = new SubscriberKey(map, email);
        Subscriber subscriber = app.storage().sub().select(key);
        subscriber.setEnabled(true);
        app.storage().sub().update(subscriber);
        return JMaps.create("subscriber", subscriber.getMap());
    }
    
}

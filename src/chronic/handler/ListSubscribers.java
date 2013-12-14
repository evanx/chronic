/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.ChronicHttpxHandler;
import chronic.entity.Subscriber;
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
        List subscribers = new LinkedList();
        for (Subscriber subscriber : app.store().listSubscribers(
                app.getEmail(httpx))) {
            subscribers.add(subscriber.getMap());
        }
        return JMaps.create("subscribers", subscribers);
    }
    
}

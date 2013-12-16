/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entitykey.UserKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class SubscribersActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(SubscribersActionAll.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        for (Subscriber subscriber : app.storage().sub().list(new UserKey(email))) {
            subscriber.setEnabled(true);
        }
        return new JMap();
    }
    
}

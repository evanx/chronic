/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.ChronicHandler;
import chronic.entity.User;
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
public class ListUsers implements ChronicHandler {

    Logger logger = LoggerFactory.getLogger(ListUsers.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        List topics = new LinkedList();
        for (User user : app.getStorage().listUsers(app.getVerifiedEmail(httpx))) {
            topics.add(user.getMap());
        }
        return JMaps.create("topics", topics);
    }
    
}

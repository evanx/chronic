/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Topic;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class TopicList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicList.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        Set topics = new HashSet();
        for (Topic topic : app.storage().listTopics(email)) {
            topics.add(topic);
        }
        if (topics.isEmpty() && app.getProperties().isDemo(httpx.getServerUrl())) {
            String adminEmail = app.getProperties().getAdminEmails().iterator().next();
            for (Topic topic : app.storage().listTopics(adminEmail)) {
                topics.add(topic);
            }
        }
        return JMaps.createMap("topics", topics);
    }
    
}

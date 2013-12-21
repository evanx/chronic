/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Topic;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class TopicList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicList.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        Set topics = new HashSet();
        for (Topic topic : httpx.db.listTopics(email)) {
            topics.add(topic);
        }
        if (topics.isEmpty() && httpx.app.getProperties().isDemo(httpx)) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (Topic topic : httpx.db.listTopics(adminEmail)) {
                topics.add(topic);
            }
        }
        httpx.injectDatabase(topics);
        return JMaps.map("topics", topics);
    }
    
}

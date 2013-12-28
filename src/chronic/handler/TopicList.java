/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        Set topics = new HashSet();
        for (Topic topic : es.listTopic(email)) {
            topics.add(topic);
        }
        if (topics.isEmpty() && httpx.getReferer().endsWith("/demo")) {
            String adminEmail = app.getProperties().getAdminEmails().iterator().next();
            for (Topic topic : es.listTopic(adminEmail)) {
                topics.add(topic);
            }
        }
        return JMaps.map("topics", topics);
    }
    
}

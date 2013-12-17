/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Topic;
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
public class TopicList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicList.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        List topics = new LinkedList();
        for (Topic topic : app.storage().listTopics(app.getEmail(httpx))) {
            topics.add(topic.getMap());
        }
        return JMaps.create("topics", topics);
    }
    
}
/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entity.Topic;
import chronic.entitykey.SubscriberKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class TopicActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicActionNone.class);
  
    ChronicApp app;
    String email;

    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        this.app = app;
        email = app.getEmail(httpx);
        List topics = new LinkedList();
        for (Topic topic : app.storage().listTopics(email)) {
            handle(topic);
            topic.setEnabled(false);
            app.storage().topic().update(topic);
            topics.add(topic.getMap());
        }
        return JMaps.mapValue("topics", topics);
    }
    
    public void handle(Topic topic) throws StorageException {
        SubscriberKey key = new SubscriberKey(topic.getId(), email);
        Subscriber subscriber = app.storage().sub().select(key);
        if (subscriber != null) {
            subscriber.setEnabled(false);
            app.storage().sub().update(subscriber);
        }
    }
    
}

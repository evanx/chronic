/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Subscriber;
import chronic.entity.Topic;
import chronic.entitykey.SubscriberKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class TopicActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicActionNone.class);
  
    ChronicHttpx httpx;
    String email;

    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        this.httpx = httpx;
        email = httpx.app.getEmail(httpx);
        List topics = new LinkedList();
        for (Topic topic : httpx.db.listTopics(email)) {
            handle(topic);
            topic.setEnabled(false);
            httpx.db.topic().replace(topic);
            topics.add(topic.getMap());
        }
        return JMaps.mapValue("topics", topics);
    }
    
    public void handle(Topic topic) throws StorageException {
        SubscriberKey key = new SubscriberKey(topic.getId(), email);
        Subscriber subscriber = httpx.db.sub().find(key);
        if (subscriber != null) {
            subscriber.setEnabled(false);
            httpx.db.sub().replace(subscriber);
        }
    }
    
}

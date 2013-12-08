/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.AdminUser;
import chronic.entity.Org;
import chronic.entity.Topic;
import chronic.entity.TopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.type.ComparableTuple;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class TopicTransaction {
    
    static Logger logger = LoggerFactory.getLogger(TopicTransaction.class);
    
    public void handle(ChronicApp app, String orgUrl, String topicString) throws StorageException {
        logger.info("handle {} {}", orgUrl, topicString);
        ComparableTuple key = Comparables.tuple(orgUrl, topicString);
        Topic topic = app.getStorage().getTopicStorage().select(key);
        if (topic == null) {
            topic = new Topic(orgUrl, topicString);
            app.getStorage().getTopicStorage().insert(topic);
        }        
    }
}

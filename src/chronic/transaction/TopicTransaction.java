/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.Topic;
import chronic.entity.TopicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class TopicTransaction {
    
    static Logger logger = LoggerFactory.getLogger(TopicTransaction.class);
    
    public void handle(ChronicApp app, String orgUrl, String networkName, String hostName, 
            String topicString) throws StorageException {
        logger.info("handle {} {}", orgUrl, topicString);
        TopicKey key = new TopicKey(orgUrl, topicString);
        Topic topic = app.getStorage().topic().select(key);
        if (topic == null) {
            topic = new Topic(orgUrl, networkName, hostName, topicString);
            app.getStorage().topic().insert(topic);
        }        
    }
}

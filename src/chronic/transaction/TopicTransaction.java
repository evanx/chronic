/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;
import chronic.entitykey.TopicOrgUnitKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class TopicTransaction {
    
    static Logger logger = LoggerFactory.getLogger(TopicTransaction.class);

    public void handle(ChronicApp app, CertKey certKey, String topicString) throws StorageException {
        logger.info("handle {} {}", certKey, topicString);
        TopicOrgUnitKey key = new TopicOrgUnitKey(certKey, topicString);
        Topic topic = app.store().topics().select(key);
        if (topic == null) {
            topic = new Topic(key);
            app.store().topics().insert(topic);
        }        
    }

}

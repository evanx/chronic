/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
import chronic.entity.Topic;
import chronic.entitykey.TopicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollTopicTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollTopicTransaction.class);

    public Topic handle(ChronicApp app, Cert cert, String topicLabel) throws StorageException {
        logger.info("handle {} {}", topicLabel, cert);
        TopicKey key = new TopicKey(cert.getId(), topicLabel);
        Topic topic = app.storage().topic().select(key);
        if (topic == null) {
            topic = new Topic(key);            
            app.storage().topic().insert(topic);
        }
        topic.setCert(cert);
        return topic;
    }

}

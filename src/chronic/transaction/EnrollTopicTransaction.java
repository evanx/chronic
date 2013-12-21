/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.api.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.Topic;
import chronic.entitykey.CertTopicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollTopicTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollTopicTransaction.class);

    public Topic handle(ChronicHttpx httpx, Cert cert, String topicLabel) throws StorageException {
        logger.info("handle {} {}", topicLabel, cert);
        CertTopicKey key = new CertTopicKey(cert.getId(), topicLabel);
        Topic topic = httpx.db.topic().find(key);
        if (topic == null) {
            topic = new Topic(key);            
            httpx.db.topic().add(topic);
        }
        topic.setCert(cert);
        return topic;
    }

}

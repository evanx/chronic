/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.api.ChronicHttpx;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import chronic.entitykey.SubscriberKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollSubscriberTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollSubscriberTransaction.class);
    
    public Subscriber handle(ChronicHttpx httpx, Topic topic, String email) throws StorageException {
        SubscriberKey key = new SubscriberKey(topic.getId(), email);
        Subscriber subscriber = httpx.db.sub().find(key);
        if (subscriber == null) {
            subscriber = new Subscriber(key);
            httpx.db.sub().add(subscriber);
        }
        return subscriber;
    }
}

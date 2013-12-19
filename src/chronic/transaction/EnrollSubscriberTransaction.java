/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
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
    
    public Subscriber handle(ChronicApp app, Topic topic, String email) throws StorageException {
        SubscriberKey key = new SubscriberKey(topic.getId(), email);
        Subscriber subscriber = app.storage().sub().select(key);
        if (subscriber == null) {
            subscriber = new Subscriber(key);
            app.storage().sub().insert(subscriber);
        }
        return subscriber;
    }
}

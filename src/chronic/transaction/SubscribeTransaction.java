/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.User;
import chronic.entity.Org;
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
public class SubscribeTransaction {
    
    static Logger logger = LoggerFactory.getLogger(SubscribeTransaction.class);
    
    public void handle(ChronicApp app, String orgDomain, String email) throws StorageException {
        logger.info("handle {} {}", orgDomain, email);
        User user = app.storage().user().select(email);
        if (user == null) {
            user = new User(email);
            app.storage().user().insert(user);
        }
        Org org = app.storage().org().select(orgDomain);
        if (org == null) {
            org = new Org(orgDomain);
            app.storage().org().insert(org);
        }
        for (Topic topic : app.storage().topic().list(org)) {
            SubscriberKey key = new SubscriberKey(topic.getTopicKey(), email);
            Subscriber subscriber = app.storage().sub().select(key);
            if (subscriber == null) {
                subscriber = new Subscriber(key);
                app.storage().sub().insert(subscriber);                
            }
        }
        
    }
}

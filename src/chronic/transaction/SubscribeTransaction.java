/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
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
    
    public void handle(ChronicApp app, String orgUrl, String email) throws StorageException {
        logger.info("handle {} {}", orgUrl, email);
        User user = app.storage().users().select(email);
        if (user == null) {
            user = new User(email);
            app.storage().users().insert(user);
        }
        Org org = app.storage().orgs().select(orgUrl);
        if (org == null) {
            org = new Org(orgUrl);
            app.storage().orgs().insert(org);
        }
        for (Topic topic : app.storage().topics().list(org)) {
            SubscriberKey key = new SubscriberKey(topic.getTopicKey(), email);
            Subscriber subscriber = app.storage().subs().select(key);
            if (subscriber == null) {
                subscriber = new Subscriber(key);
                app.storage().subs().insert(subscriber);                
            }
        }
        
    }
}

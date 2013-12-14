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
        User user = app.store().users().select(email);
        if (user == null) {
            user = new User(email);
            app.store().users().insert(user);
        }
        Org org = app.store().orgs().select(orgUrl);
        if (org == null) {
            org = new Org(orgUrl);
            app.store().orgs().insert(org);
        }
        for (Topic topic : app.store().topics().list(org)) {
            SubscriberKey key = new SubscriberKey(topic.getTopicKey(), email);
            Subscriber subscriber = app.store().subs().select(key);
            if (subscriber == null) {
                subscriber = new Subscriber(key);
                app.store().subs().insert(subscriber);                
            }
        }
        
    }
}

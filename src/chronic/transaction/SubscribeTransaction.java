/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.User;
import chronic.entity.Org;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import chronic.entity.SubscriberKey;
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
        User user = app.getStorage().getUserStorage().select(email);
        if (user == null) {
            user = new User(email);
            app.getStorage().getUserStorage().insert(user);
        }
        Org org = app.getStorage().getOrgStorage().select(orgUrl);
        if (org == null) {
            org = new Org(orgUrl);
            app.getStorage().getOrgStorage().insert(org);
        }
        for (Topic topic : app.getStorage().listTopics(org)) {
            SubscriberKey key = new SubscriberKey(org.getOrgUrl(), topic.getTopicString(), email);
            Subscriber subscriber = app.getStorage().getSubscriberStorage().select(key);
            if (subscriber == null) {
                subscriber = new Subscriber(key);
                app.getStorage().getSubscriberStorage().insert(subscriber);                
            }
        }
        
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.User;
import chronic.entity.UserRoleType;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.TopicSubscriber;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.type.ComparableTuple;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class SubscribeTransaction {
    
    static Logger logger = LoggerFactory.getLogger(SubscribeTransaction.class);
    
    public void handle(ChronicApp app, String orgUrl, String email) throws StorageException {
        logger.info("handle {} {}", orgUrl, email);
        ComparableTuple key = Comparables.tuple(orgUrl, email);
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
            TopicSubscriber subscriber = app.getStorage().getSubscriberStorage().
                    select(Comparables.tuple(org.getUrl(), topic.getTopicString(), email));
            if (subscriber == null) {
                subscriber = new TopicSubscriber(orgUrl, topic.getTopicString(), email);
                app.getStorage().getSubscriberStorage().insert(subscriber);                
            }
        }
        
    }
}
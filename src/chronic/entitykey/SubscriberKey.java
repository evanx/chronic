/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class SubscriberKey extends ComparableTuple {
    Long topicId;
    String email;

    public SubscriberKey(Long topicId, String email) {
        super(topicId, email);
        this.topicId = topicId;
        this.email = email;
    }

    public Long getTopicId() {
        return topicId;
    }
        
    public String getEmail() {
        return email;
    }
}

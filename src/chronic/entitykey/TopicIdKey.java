/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.util.ComparableValue;

/**
 *
 * @author evan.summers
 */
public final class TopicIdKey extends ComparableValue {
    Long topicId;

    public TopicIdKey(Long topicId) {
        super(topicId);
        this.topicId = topicId;
    }

    public Long getTopicId() {
        return topicId;
    }
}

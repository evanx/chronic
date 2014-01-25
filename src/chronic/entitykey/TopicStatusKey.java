/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import chronic.type.StatusType;
import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class TopicStatusKey extends ComparableTuple {
    Long topicId;
    StatusType statusType;

    public TopicStatusKey(Long topicId, StatusType statusType) {
        super(topicId, statusType);
        this.statusType = statusType;
    }

    public Long getTopicId() {
        return topicId;
    }

    public StatusType getStatusType() {
        return statusType;
    }        
}

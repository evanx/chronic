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
public final class TopicMetricKey extends ComparableTuple {
    Long topicId;
    int order; 
    String metricLabel;
    
    public TopicMetricKey(Long topicId, int order, String metricLabel) {
        super(topicId, order, metricLabel);
        this.topicId = topicId;
        this.order = order;
        this.metricLabel = metricLabel;
    }

    public Long getTopicId() {
        return topicId;
    }
    
    public String getMetricLabel() {
        return metricLabel;
    }
}

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
    String metricLabel;
    int order;
    
    public TopicMetricKey(Long topicId, String metricLabel) {
        super(topicId, metricLabel);
        this.topicId = topicId;
        this.metricLabel = metricLabel;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
   
    public Long getTopicId() {
        return topicId;
    }
    
    public String getMetricLabel() {
        return metricLabel;
    }
}

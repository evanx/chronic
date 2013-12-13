/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class TopicKey extends ComparableTuple {
    String orgUrl;
    String topicString;
            
    public TopicKey(String orgUrl, String topicString) {
        super(orgUrl, topicString);
        this.orgUrl = orgUrl;
        this.topicString = topicString;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getTopicString() {
        return topicString;
    }

}

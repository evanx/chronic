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
public final class OrgTopicKey extends ComparableTuple {
    String orgUrl;
    String topicString;
            
    public OrgTopicKey(String orgUrl, String topicString) {
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

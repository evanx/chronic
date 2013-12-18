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
    String orgDomain;
    String topicString;
            
    public OrgTopicKey(String orgDomain, String topicString) {
        super(orgDomain, topicString);
        this.orgDomain = orgDomain;
        this.topicString = topicString;
    }

    public String getOrgDomain() {
        return orgDomain;
    }

    public String getTopicString() {
        return topicString;
    }

}

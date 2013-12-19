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
    String topicLabel;
            
    public OrgTopicKey(String orgDomain, String topicLabel) {
        super(orgDomain, topicLabel);
        this.orgDomain = orgDomain;
        this.topicLabel = topicLabel;
    }

    public String getOrgDomain() {
        return orgDomain;
    }

    public String getTopicLabel() {
        return topicLabel;
    }

}

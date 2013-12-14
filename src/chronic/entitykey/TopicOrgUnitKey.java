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
public final class TopicOrgUnitKey extends ComparableTuple {
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;
            
    public TopicOrgUnitKey(String orgUrl, String orgUnit, String commonName, String topicString) {
        super(orgUrl, orgUnit, commonName, topicString);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
    }

    public TopicOrgUnitKey(CertKey certKey, String topicString) {
        this(certKey.getOrgUrl(), certKey.getOrgUnit(), certKey.getCommonName(), topicString);
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public String getCommonName() {
        return commonName;
    }
    
    public String getTopicString() {
        return topicString;
    }

}

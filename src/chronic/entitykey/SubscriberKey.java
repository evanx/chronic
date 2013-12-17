/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;
import vellum.jx.JMap;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public final class SubscriberKey extends ComparableTuple {
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;
    String email;

    public SubscriberKey(JMap map, String email) throws JMapException {
        this(map.getString("orgUrl"), 
                map.getString("orgUnit"), 
                map.getString("commonName"),
                map.getString("topicString"),
                email);
    }
    
    public SubscriberKey(String orgUrl, String orgUnit, String commonName, 
            String topicString, String email) {
        super(orgUrl, orgUnit, commonName, topicString, email);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
        this.email = email;
    }

    
    public SubscriberKey(TopicKey key, String email) {
        this(key.getOrgUrl(), key.getOrgUnit(), key.getCommonName(), key.getTopicString(), email);
    }
    
    public SubscriberKey(CertKey certKey, String topicString, String email) {
        this(new TopicKey(certKey, topicString), email);
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

    public String getEmail() {
        return email;
    }
}

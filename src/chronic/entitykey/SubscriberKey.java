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
    String orgDomain;
    String orgUnit;
    String commonName;
    String topicString;
    String email;

    public SubscriberKey(JMap map, String email) throws JMapException {
        this(map.getString("orgDomain"), 
                map.getString("orgUnit"), 
                map.getString("commonName"),
                map.getString("topicString"),
                email);
    }
    
    public SubscriberKey(String orgDomain, String orgUnit, String commonName, 
            String topicString, String email) {
        super(orgDomain, orgUnit, commonName, topicString, email);
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
        this.email = email;
    }

    
    public SubscriberKey(TopicKey key, String email) {
        this(key.getOrgDomain(), key.getOrgUnit(), key.getCommonName(), key.getTopicString(), email);
    }
    
    public SubscriberKey(CertKey certKey, String topicString, String email) {
        this(new TopicKey(certKey, topicString), email);
    }

    public String getOrgDomain() {
        return orgDomain;
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

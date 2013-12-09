/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class SubscriberKey extends ComparableTuple {
    String orgUrl;
    String topicString;
    String email;
            
    public SubscriberKey(String orgUrl, String topicString, String email) {
        super(orgUrl, topicString, email);
        this.orgUrl = orgUrl;
        this.topicString = topicString;
        this.email = email;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getTopicString() {
        return topicString;
    }

    public String getEmail() {
        return email;
    }
    
    
}

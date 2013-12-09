/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitytype.TopicAction;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;

/**
 *
 * @author evan.summers
 */
public final class Subscriber extends AbstractIdEntity {
    Long id;
    String orgUrl;
    String topicString;
    String email;
    boolean enabled = false;
            
    public Subscriber() {
    }

    public Subscriber(SubscriberKey key) {
        this.orgUrl = key.getOrgUrl();
        this.topicString = key.getTopicString();
        this.email = key.getEmail();
    }
    
    public Subscriber(String orgUrl, String topicString, String email) {
        this.topicString = topicString;
        this.orgUrl = orgUrl;
        this.email = email;
    }

    @Override
    public Comparable getKey() {
        return new SubscriberKey(orgUrl, topicString, email);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }

    public String getOrgUrl() {
        return orgUrl;
    }
    
    public String getTopicString() {
        return topicString;
    }
        
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgName", orgUrl);
        map.put("topicString", topicString);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("email", email);
        return map;
    }

    private TopicAction getAction() {
        return enabled ? TopicAction.UNSUBSCRIBE : TopicAction.SUBSCRIBE;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
    
}

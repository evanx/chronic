/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public final class TopicSubscriber extends AbstractIdEntity {
    Long id;
    String orgUrl;
    String topicString;
    String email;
    boolean enabled = false;
            
    public TopicSubscriber() {
    }

    public TopicSubscriber(String orgUrl, String topicString, String email) {
        this.topicString = topicString;
        this.orgUrl = orgUrl;
        this.email = email;
    }

    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, topicString, email);
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
        map.put("orgUrl", orgUrl);
        map.put("topicString", topicString);
        map.put("email", email);
        return map;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

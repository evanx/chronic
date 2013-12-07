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
    String topic;
    String email;
    boolean enabled = false;
            
    public TopicSubscriber() {
    }

    public TopicSubscriber(String orgUrl, String topic, String email) {
        this.topic = topic;
        this.orgUrl = orgUrl;
        this.email = email;
    }

    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, topic, email);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgUrl", orgUrl);
        map.put("topic", topic);
        map.put("email", email);
        return map;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

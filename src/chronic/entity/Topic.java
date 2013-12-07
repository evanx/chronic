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
public final class Topic extends AbstractIdEntity {
    Long id;
    String orgUrl;
    String topic;
    boolean enabled = true;
            
    public Topic() {
    }

    public Topic(String orgUrl, String topic) {
        this.orgUrl = orgUrl;
        this.topic = topic;
    }

    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, topic);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrgName() {
        return orgUrl;
    }

    public void setOrgName(String orgName) {
        this.orgUrl = orgName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgUrl", orgUrl);
        map.put("topic", topic);
        return map;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

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
    String topicString;
    boolean enabled = true;
            
    public Topic() {
    }

    public Topic(String orgUrl, String topicString) {
        this.orgUrl = orgUrl;
        this.topicString = topicString;
    }

    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, topicString);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
        
    public String getOrgUrl() {
        return orgUrl;
    }

    public String getTopicString() {
        return topicString;
    }

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgUrl", orgUrl);
        map.put("topicString", topicString);
        return map;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.TopicOrgKey;
import chronic.entitykey.TopicOrgKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.TopicOrgUnitKey;
import chronic.entitytype.TopicAction;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public final class Topic extends AbstractIdEntity implements OrgKeyed, TopicOrgKeyed, Enabled {

    Long id;
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;
    boolean enabled = true;

    public Topic(TopicOrgUnitKey key) {
        this.orgUrl = key.getOrgUrl();
        this.orgUnit = key.getOrgUnit();
        this.commonName = key.getCommonName();
        this.topicString = key.getTopicString();
    }

    public Topic(CertKey key, String topicString) {
        this.orgUrl = key.getOrgUrl();
        this.orgUnit = key.getOrgUnit();
        this.commonName = key.getCommonName();
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

    @Override
    public boolean isEnabled() {
        return enabled;
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
        map.put("networkName", orgUnit);
        map.put("hostName", commonName);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicString", topicString);
        return map;
    }

    private TopicAction getAction() {
        return enabled ? TopicAction.UNSUBSCRIBE : TopicAction.SUBSCRIBE;
    }

    @Override
    public String toString() {
        return getMap().toString();
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgUrl);
    }

    @Override
    public TopicOrgKey getTopicOrgKey() {
        return new TopicOrgKey(orgUrl, topicString);
    }
}

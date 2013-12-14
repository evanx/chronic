/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.OrgKeyed;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitytype.TopicAction;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public final class Topic extends AbstractIdEntity implements OrgKeyed, TopicKeyed {

    Long id;
    String orgUrl;
    String networkName;
    String hostName;
    String topicString;
    boolean enabled = true;

    public Topic() {
    }

    public Topic(String orgUrl, String networkName, String hostName, String topicString) {
        this.orgUrl = orgUrl;
        this.networkName = networkName;
        this.hostName = hostName;
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
        map.put("networkName", networkName);
        map.put("hostName", hostName);
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
    public TopicKey getTopicKey() {
        return new TopicKey(orgUrl, topicString);
    }
}

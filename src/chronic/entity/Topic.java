/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgTopicKey;
import chronic.entitykey.OrgTopicKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgUnitKey;
import chronic.entitykey.OrgUnitKeyed;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.entitytype.TopicAction;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public final class Topic extends AbstractIdEntity implements TopicKeyed, OrgKeyed, 
        OrgUnitKeyed, CertKeyed, OrgTopicKeyed, Enabled {

    Long id;
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;
    boolean enabled = true;

    public Topic(String orgUrl, String orgUnit, String commonName, String topicString) {
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
    }
    
    public Topic(TopicKey key) {
        this(key.getOrgUrl(), key.getOrgUnit(), key.getCommonName(), key.getTopicString());
    }

    public Topic(CertKey key, String topicString) {
        this(key.getOrgUrl(), key.getOrgUnit(), key.getCommonName(), topicString);
    }

    @Override
    public Comparable getKey() {
        return getTopicKey();
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(orgUrl, orgUnit, commonName, topicString);
    }    

    @Override
    public OrgTopicKey getOrgTopicKey() {
        return new OrgTopicKey(orgUrl, topicString);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgUrl);
    }
    
    @Override
    public OrgUnitKey getOrgUnitKey() {
        return new OrgUnitKey(orgUrl, orgUnit);
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgUrl, orgUnit, commonName);
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
}

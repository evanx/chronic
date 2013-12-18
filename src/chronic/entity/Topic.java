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
import chronic.entitytype.TopicActionType;
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
    String orgDomain;
    String orgUnit;
    String commonName;
    String topicString;
    boolean enabled = true;

    public Topic(String orgDomain, String orgUnit, String commonName, String topicString) {
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
    }
    
    public Topic(TopicKey key) {
        this(key.getOrgDomain(), key.getOrgUnit(), key.getCommonName(), key.getTopicString());
    }

    public Topic(CertKey key, String topicString) {
        this(key.getOrgDomain(), key.getOrgUnit(), key.getCommonName(), topicString);
    }

    @Override
    public Comparable getKey() {
        return getTopicKey();
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(orgDomain, orgUnit, commonName, topicString);
    }    

    @Override
    public OrgTopicKey getOrgTopicKey() {
        return new OrgTopicKey(orgDomain, topicString);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
    }
    
    @Override
    public OrgUnitKey getOrgUnitKey() {
        return new OrgUnitKey(orgDomain, orgUnit);
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgDomain, orgUnit, commonName);
    }
    
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public String getOrgDomain() {
        return orgDomain;
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
        map.put("orgDomain", orgDomain);
        map.put("networkName", orgUnit);
        map.put("hostName", commonName);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicString", topicString);
        return map;
    }

    private TopicActionType getAction() {
        return enabled ? TopicActionType.DISABLE : TopicActionType.SUBSCRIBE;
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.UserKeyed;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.UserKey;
import chronic.entitykey.OrgTopicKey;
import chronic.entitykey.OrgTopicKeyed;
import chronic.entitykey.SubscriberKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.entitytype.SubscriberActionType;
import chronic.entitytype.TopicActionType;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public final class Subscriber extends AbstractIdEntity implements SubscriberKeyed, 
        OrgKeyed, UserKeyed, TopicKeyed, OrgTopicKeyed, CertKeyed, Enabled {
    Long id;
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;
    String email;
    boolean enabled = true;
            
    public Subscriber() {
    }

    public Subscriber(SubscriberKey key) {
        this.orgUrl = key.getOrgUrl();
        this.orgUnit = key.getOrgUnit();
        this.commonName = key.getCommonName();
        this.topicString = key.getTopicString();
        this.email = key.getEmail();
    }
    
    @Override
    public Comparable getKey() {
        return getSubscriberKey();
    }

    @Override
    public SubscriberKey getSubscriberKey() {
        return new SubscriberKey(orgUrl, orgUnit, commonName, topicString, email);
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(orgUrl, orgUnit, commonName, topicString);
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgUrl, orgUnit, commonName);
    }
    
    @Override
    public UserKey getUserKey() {
        return new UserKey(email);
    }

    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgUrl);
    }

    @Override
    public OrgTopicKey getOrgTopicKey() {
        return new OrgTopicKey(orgUrl, topicString);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        map.put("topicString", topicString);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("email", email);
        return map;
    }

    private SubscriberActionType getAction() {
        return enabled ? SubscriberActionType.UNSUBSCRIBE : SubscriberActionType.SUBSCRIBE;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
    
}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.app.ChronicApp;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.SubscriberKeyed;
import chronic.entitykey.TopicIdKey;
import chronic.entitykey.TopicIdKeyed;
import chronic.entitykey.UserKey;
import chronic.entitykey.UserKeyed;
import chronic.entitytype.ChronicApped;
import chronic.entitytype.SubscriberActionType;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.storage.AbstractIdEntity;
import vellum.storage.StorageException;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public final class Subscriber extends AbstractIdEntity implements SubscriberKeyed, UserKeyed, 
        TopicIdKeyed, Enabled, JMapped, ChronicApped {
    Long id;
    Long topicId;
    String email;
    boolean enabled = true;
    
    transient Topic topic;
    
    public Subscriber() {
    }

    public Subscriber(SubscriberKey key) {
        this.topicId = key.getTopicId();
        this.email = key.getEmail();
    }
    
    @Override
    public Comparable getKey() {
        return getSubscriberKey();
    }

    @Override
    public SubscriberKey getSubscriberKey() {
        return new SubscriberKey(topicId, email);
    }

    @Override
    public TopicIdKey getTopicIdKey() {
        return new TopicIdKey(topicId);
    }
        
    @Override        
    public UserKey getUserKey() {
        return new UserKey(email);
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

    public Long getTopicId() {
        return topicId;
    }
    
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("topicId", topicId);
        map.put("email", email);
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicLabel", topic.getTopicLabel());
        map.put("orgDomain", topic.getCert().getOrgDomain());
        topic.getCert().put(map);
        return map;
    }

    @Override
    public void inject(ChronicApp app) throws StorageException {
        topic = app.storage().topic().retrieve(topicId);
        topic.inject(app);
    }

    private SubscriberActionType getAction() {
        return enabled ? SubscriberActionType.UNSUBSCRIBE : SubscriberActionType.SUBSCRIBE;
    }
    
    @Override
    public String toString() {
        return getKey().toString();
    }
}

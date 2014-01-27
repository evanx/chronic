/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.SubscriptionKey;
import chronic.entitykey.SubscriptionKeyed;
import chronic.entitytype.SubscriptionActionType;
import java.io.Serializable;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.entity.ComparableEntity;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.type.Enabled;
import vellum.util.Calendars;

/**
 *
 * @author evan.summers
 */
@Entity()
@Table(name = "topic_sub")
public class Subscription extends ComparableEntity implements SubscriptionKeyed, 
        Enabled, JMapped, Serializable {
    
    static Logger logger = LoggerFactory.getLogger(Subscription.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "topic_sub_id")
    Long id;

    @Column()    
    boolean enabled;
    
    @Column(name = "timezone_id") 
    String timeZoneId;
    
    @ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
    Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name = "email", referencedColumnName = "email")
    Person person;
    
    public Subscription() {
    }

    public Subscription(Topic topic, Person person) {
        this.topic = topic;
        this.person = person;
    }
    
    @Override
    public Comparable getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public Topic getTopic() {
        return topic;
    }

    public Person getPerson() {
        return person;
    }
    
    @Override
    public JMap getMap() {
        logger.info("getMap {} {}", id, getTopic());
        assert getTopic() != null;
        assert getTopic().getCert() != null;
        JMap map = getTopic().getCert().getKeyMap();
        map.put("id", id);
        map.put("topicId", getTopic().getId());
        map.put("email", getPerson().getId());
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicLabel", topic.getTopicLabel());
        map.put("orgDomain", topic.getCert().getOrgDomain());
        return map;
    }

    private SubscriptionActionType getAction() {
        return enabled ? SubscriptionActionType.UNSUBSCRIBE : SubscriptionActionType.SUBSCRIBE;
    }
        
    public TimeZone getTimeZone() {
        return Calendars.getTimeZone(timeZoneId);
    }

    @Override
    public SubscriptionKey getSubscriptionKey() {
        assert topic.getId() != 0;
        return new SubscriptionKey(topic.getId(), person.getEmail());
    }
    
    @Override
    public String toString() {
        return getSubscriptionKey().toString();
    }    
}

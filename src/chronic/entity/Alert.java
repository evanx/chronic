/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.SubscriptionKey;
import chronic.entitykey.SubscriptionKeyed;
import chronic.type.StatusType;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import vellum.entity.ComparableEntity;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
@Entity
public class Alert extends ComparableEntity implements SubscriptionKeyed, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "alert_id")
    Long id;

    @Column(length = 64)
    String email;
    
    @Column(name = "status", length = 32)
    @Enumerated(EnumType.STRING)
    StatusType statusType;
    
    @Column(name = "occurred")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar occurred;

    @Column(name = "alerted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar alerted = Calendar.getInstance();
    
    @ManyToOne()
    @JoinColumn(name = "email", referencedColumnName = "email",
            insertable = false, updatable = false)
    Person person;

    @ManyToOne()
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id",
            insertable = false, updatable = false)
    Topic topic;
        
    public Alert() {
    }

    public Alert(Topic topic, StatusType statusType, Calendar occurred, String email) {
        this.topic = topic;
        this.statusType = statusType;
        this.occurred = occurred;
        this.email = email;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public String getEmail() {
        return email;
    }        

    public Calendar getAlerted() {
        return alerted;
    }

    public Calendar getOccurred() {
        return occurred;
    }
    
    @Override
    public SubscriptionKey getSubscriptionKey() {
        return new SubscriptionKey(topic.getId(), email);
    }

    @Override
    public String toString() {
        return Args.format(topic.getTopicLabel(), statusType, email, alerted);
    }
        
}

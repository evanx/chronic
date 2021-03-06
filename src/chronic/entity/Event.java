/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import vellum.entity.ComparableEntity;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
@Entity
@Table(name = "event")
public class Event extends ComparableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    Long id;

    @Column(name = "status", length = 32)
    @Enumerated(EnumType.STRING)
    StatusType statusType;
    
    @Column(length = 4096)
    String content;
    
    @Column(name = "occurred")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar occurred;

    @ManyToOne()
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
    Topic topic;
        
    public Event() {
    }

    public Event(Topic topic, StatusType statusType, Calendar occurred) {
        this.topic = topic;
        this.statusType = statusType;
        this.occurred = occurred;
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

    @Override
    public String toString() {
        return Args.format(topic, statusType, occurred);
    }

    
}

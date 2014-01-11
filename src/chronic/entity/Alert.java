/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import vellum.entity.ComparableEntity;

/**
 *
 * @author evan.summers
 */
@Entity
public class Alert extends ComparableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "alert_id")
    Long id;
        
    @ManyToOne()
    @JoinColumn(name = "person_id", referencedColumnName = "person_id",
            insertable = false, updatable = false)
    Person person;

    @ManyToOne()
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id",
            insertable = false, updatable = false)
    Topic topic;
    
    @Column(name = "alerted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Calendar alerted = Calendar.getInstance();
    
    public Alert() {
    }

    public Alert(Person person, Topic topic) {
        this.person = person;
        this.topic = topic;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public Person getPerson() {
        return person;
    }
    
}

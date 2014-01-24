/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.type.AlertType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import vellum.entity.ComparableEntity;
import vellum.jx.JMap;
import vellum.jx.JMapped;

/**
 *
 * @author evan.summers
 */
@Entity
public class Topic extends ComparableEntity implements TopicKeyed, JMapped, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "topic_id")
    Long id;
    
    @Column(name = "cert_id")
    Long certId;
    
    @Column(name = "topic_label")
    String topicLabel;

    @Column(name = "alert_type", length = 32)
    @Enumerated(EnumType.STRING)
    AlertType alertType;

    @Column(name = "period_secs")
    long periodSeconds;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cert_id", referencedColumnName = "cert_id",
            insertable = false, updatable = false)
    Cert cert;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "topic")
    List<Subscription> subscriptions;
    
    public Topic() {
    }
                
    public Topic(Long certId, String topicLabel) {
        this.certId = certId;
        this.topicLabel = topicLabel;
    }
    
    public Topic(TopicKey key) {
        this(key.getCertId(), key.getTopicLabel());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(certId, topicLabel);
    }    

    public Long getCertId() {
        return certId;
    }

    public void setCertId(Long certId) {
        this.certId = certId;
    }
    
    public void setCert(Cert cert) {
        this.cert = cert;
        if (cert != null) {
            certId = cert.id;
        }
    }

    public Cert getCert() {
        return cert;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
    
    public String getTopicLabel() {
        return topicLabel;
    }

    public void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setPeriodSeconds(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }
        
    @Override
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("certId", cert.getId());
        map.put("orgDomain", cert.getOrgDomain());
        map.put("orgUnit", cert.getOrgUnit());
        map.put("commonName", cert.getCommonName());
        map.put("topicLabel", topicLabel);
        return map;
    }
    
    @Override
    public String toString() {
        return getId().toString();
    }

}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.app.ChronicDatabase;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.CertTopicKeyed;
import chronic.entitytype.ChronicDatabaseInjectable;
import chronic.entitytype.TopicActionType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.storage.AutoIdEntity;
import vellum.storage.StorageException;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
@Entity
public class Topic extends AutoIdEntity implements CertTopicKeyed,  
        JMapped, Enabled, ChronicDatabaseInjectable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "topic_id")
    Long id;
    
    @Column(name = "cert_id")
    Long certId;
    
    @Column(name = "topic_label")
    String topicLabel;
    
    @Column()
    boolean enabled = true;
    
    transient Cert cert;

    public Topic() {
    }
                
    public Topic(Long certId, String topicLabel) {
        this.certId = certId;
        this.topicLabel = topicLabel;
    }
    
    public Topic(CertTopicKey key) {
        this(key.getCertId(), key.getTopicLabel());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    
    @Override
    public CertTopicKey getCertTopicKey() {
        return new CertTopicKey(certId, topicLabel);
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
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public String getTopicLabel() {
        return topicLabel;
    }

    public void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }
    
    @Override
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("certId", cert.getId());
        map.put("orgDomain", cert.getOrgDomain());
        map.put("orgUnit", cert.getOrgUnit());
        map.put("commonName", cert.getCommonName());
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicLabel", topicLabel);
        return map;
    }

    private TopicActionType getAction() {
        return enabled ? TopicActionType.UNSUBSCRIBE : TopicActionType.SUBSCRIBE;
    }

    @Override
    public void inject(ChronicDatabase db) throws StorageException {
        cert = db.cert().retrieve(certId);
    }
    
    @Override
    public String toString() {
        return getId().toString();
    }

}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.app.ChronicApp;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.CertTopicKeyed;
import chronic.entitykey.TopicIdKeyed;
import chronic.entitytype.ChronicApped;
import chronic.entitytype.TopicActionType;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.storage.AbstractIdEntity;
import vellum.storage.StorageException;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public final class Topic extends AbstractIdEntity implements CertTopicKeyed,  
        JMapped, Enabled, ChronicApped {

    Long id;
    Long certId;
    String topicLabel;
    boolean enabled = true;
    transient Cert cert;
            
    public Topic(Long certId, String topicLabel) {
        this.certId = certId;
        this.topicLabel = topicLabel;
    }
    
    public Topic(CertTopicKey key) {
        this(key.getCertId(), key.getTopicLabel());
    }

    @Override
    public Comparable getKey() {
        return getTopicKey();
    }

    @Override
    public CertTopicKey getTopicKey() {
        return new CertTopicKey(certId, topicLabel);
    }    

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
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
            certId = cert.getId();
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
    public void inject(ChronicApp app) throws StorageException {
        cert = app.storage().cert().find(certId);
    }
    
    @Override
    public String toString() {
        return getKey().toString();
    }
}

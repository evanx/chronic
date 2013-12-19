/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.app.ChronicApp;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
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
public final class Topic extends AbstractIdEntity implements TopicKeyed, JMapped, Enabled, ChronicApped {

    Long id;
    Long certId;
    String topicLabel;
    boolean enabled = true;
    transient Cert cert;
            
    public Topic(Long certId, String topicLabel) {
        this.certId = certId;
        this.topicLabel = topicLabel;
    }
    
    public Topic(TopicKey key) {
        this(key.getCertId(), key.getTopicLabel());
    }

    @Override
    public Comparable getKey() {
        return getTopicKey();
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(certId, topicLabel);
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
        map.put("orgDomain", cert.getOrgDomain());
        map.put("orgUnit", cert.getOrgUnit());
        map.put("commonName", cert.getCommonName());
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        map.put("topicLabel", topicLabel);
        return map;
    }

    private TopicActionType getAction() {
        return enabled ? TopicActionType.DISABLE : TopicActionType.SUBSCRIBE;
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

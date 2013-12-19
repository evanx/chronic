/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;
import vellum.jx.JMap;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public final class CertTopicKey extends ComparableTuple {
    Long certId;
    String topicLabel;

    public CertTopicKey(JMap map) throws JMapException {
        this(map.getLong("certId"), map.getString("topicLabel"));
    }
    
    public CertTopicKey(Long certId, String topicLabel) {
        super(certId, topicLabel);
        this.certId = certId;
        this.topicLabel = topicLabel;
    }

    public Long getCertId() {
        return certId;
    }
    
    public String getTopicLabel() {
        return topicLabel;
    }
}

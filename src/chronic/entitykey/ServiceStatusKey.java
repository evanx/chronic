/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import chronic.type.StatusType;
import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class ServiceStatusKey extends ComparableTuple {
    Long certId;
    String topicLabel;
    StatusType statusType;

    public ServiceStatusKey(Long certId, String topicLabel, StatusType statusType) {
        super(certId, topicLabel, statusType);
        this.certId = certId;
        this.topicLabel = topicLabel;
        this.statusType = statusType;
    }

    public Long getCertId() {
        return certId;
    }
    
    public String getTopicLabel() {
        return topicLabel;
    }

    public StatusType getStatusType() {
        return statusType;
    }        
}

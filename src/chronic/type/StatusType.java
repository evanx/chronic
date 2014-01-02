/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.type;

import chronic.bundle.Bundle;

/**
 *
 * @author evan.summers
 */
public enum StatusType {            
    UNKNOWN,    
    OK,
    WARNING,
    CRITICAL,
    CONTENT_CHANGED,
    ELAPSED,
    RESUMED,
    CONTENT_ERROR;

    public boolean isStatusAlertable() {
        return this == OK || this == WARNING || this == CRITICAL;
    }

    public boolean isStatusKnown() {
        return this != UNKNOWN;
    }

    public boolean isStatusGreater(StatusType statusType) {
        return statusType == null || statusType == UNKNOWN || statusType.ordinal() < ordinal();
    }
    
    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }
    
    
}

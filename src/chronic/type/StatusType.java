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
    OK,
    WARNING,
    CRITICAL,
    CONTENT_CHANGED,
    ELAPSED,
    RESUMED,
    UNKNOWN;    

    public boolean isStatusAlertable() {
        return ordinal() <= CRITICAL.ordinal();
    }

    public boolean isStatusKnown() {
        return ordinal() <= UNKNOWN.ordinal();
    }
    
    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }
    
    
}

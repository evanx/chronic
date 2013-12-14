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
    UNKNOWN,
    CONTENT_CHANGED,
    ELAPSED,
    RESUMED;

    public boolean isAlertable() {
        return ordinal() < UNKNOWN.ordinal();
    }
    
    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }
    
    
}

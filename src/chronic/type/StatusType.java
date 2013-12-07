/*
 * Source https://code.google.com/p/vellum by @evanxsummers
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
    ELAPSED;

    public boolean isAlertable() {
        return this != UNKNOWN;
    }
    
    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }
    
}

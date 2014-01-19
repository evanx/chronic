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
    CONTENT_ERROR,
    UNKNOWN,    
    OK,
    WARNING,
    CRITICAL,
    CONTENT_CHANGED,
    ELAPSED,
    RESUMED;

    public boolean isKnown() {
        return ordinal() > UNKNOWN.ordinal();
    }
        
    public boolean isStatus() {
        return this == OK || this == WARNING || this == CRITICAL;
    }

    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }

}

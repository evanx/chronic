/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.type;

import chronic.bundle.Bundle;
import vellum.type.Labelled;

/**
 *
 * @author evan.summers
 */
public enum AlertType implements Labelled {
    STATUS_CHANGED,
    CONTENT_CHANGED,
    NEVER,
    WARNING,
    ERROR,
    PATTERN,
    SERVER_RESTARTED,
    ALWAYS;
    
    public boolean isPollable() {
        return ordinal() <= CONTENT_CHANGED.ordinal();
    }
    
    public static AlertType parse(String string) {
        for (AlertType type : values()) {
            if (string.equalsIgnoreCase(type.name())) {
                return type;
            }            
        }        
        return null;
    }
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
    
}

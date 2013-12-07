/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.type;

import chronic.bundle.Bundle;
import vellum.format.Labelled;

/**
 *
 * @author evan.summers
 */
public enum AlertType implements Labelled {
    NONE,
    WARNING,
    ERROR,
    PATTERN,
    ELAPSED,
    STATUS_CHANGED,
    CONTENT_CHANGED,
    ALWAYS;
    
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
        return Bundle.get(AlertType.class).getString(name());
    }
}

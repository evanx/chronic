/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.type;

import vellum.bundle.Bundle;
import vellum.type.Labelled;

/**
 *
 * @author evan.summers
 */
public enum AlertType implements Labelled {
    STATUS_CHANGED,
    CONTENT_CHANGED,
    ALWAYS,
    WARNING,
    ERROR,
    PATTERN,
    NEVER,
    SERVER_RESTARTED;

    public boolean isPollable() {
        return ordinal() < ALWAYS.ordinal();
    }
    
    public boolean isAlertable() {
        return ordinal() < NEVER.ordinal();
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

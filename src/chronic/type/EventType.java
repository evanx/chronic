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
public enum EventType implements Labelled {
    ERROR,
    INITIAL;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }    

    public boolean isAlertable() {
        return false;
    }
        
}

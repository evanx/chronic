/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.bundle.Bundle;
import vellum.format.Labelled;

/**
 *
 * @author evan.summers
 */
public enum UserAction implements Labelled {
    CONFIRM,    
    REVOKE;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
        
}

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
public enum TopicAction implements Labelled {
    UNSUBSCRIBE,    
    SUBSCRIBE;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
        
}

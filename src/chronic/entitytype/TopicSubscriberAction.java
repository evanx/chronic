/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.bundle.Bundle;
import vellum.format.Labelled;

/**
 *
 * @author evan.summers
 */
public enum TopicSubscriberAction implements Labelled {
    UNSUBSCRIBE,    
    SUBSCRIBE;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
        
}

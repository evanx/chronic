/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import vellum.bundle.Bundle;
import vellum.type.Labelled;

/**
 *
 * @author evan.summers
 */
public enum SubscriptionActionType implements Labelled {
    UNSUBSCRIBE,    
    SUBSCRIBE;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
        
}

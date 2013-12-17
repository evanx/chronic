/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.bundle.Bundle;
import vellum.type.Labelled;

/**
 *
 * @author evan.summers
 */
public enum TopicActionType implements Labelled {
    DISABLE,    
    SUBSCRIBE;
    
    @Override
    public String getLabel() {
        return Bundle.get(getClass()).getString(name());
    }
    
        
}

/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.type;

import chronic.bundle.Bundle;
import java.util.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public enum StatusType {            
    OK,
    WARNING,
    CRITICAL,
    UNKNOWN,
    CONTENT_CHANGED,
    RESUMED,
    ELAPSED;

    public boolean isAlertable() {
        return this != UNKNOWN;
    }
    
    public String getLabel() {
        return Bundle.get(StatusType.class).getString(name());
    }
    
    
}

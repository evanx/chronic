/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.type;

/**
 *
 * @author evan.summers
 */
public enum AlertType {
    WARN,
    ERROR,
    PATTERN,
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
}
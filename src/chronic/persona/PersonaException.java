/*
 */
package chronic.persona;

/**
 *
 * @author evan.summers
 */
public class PersonaException extends Exception {

    public PersonaException(String message) {
        super(message);
    }

    public PersonaException(String status, String reason) {
        this(String.format("%s: %s", status, reason));
    }
}

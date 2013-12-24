/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.util.ComparableValue;

/**
 *
 * @author evan.summers
 */
public final class PersonIdKey extends ComparableValue {
    Long id;

    public PersonIdKey(Long id) {
        super(id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
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
public final class CertIdKey extends ComparableValue {
    Long id;

    public CertIdKey(Long id) {
        super(id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

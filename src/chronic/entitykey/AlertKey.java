/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class AlertKey extends ComparableTuple {
    Long eventId;
    Long personId;

    public AlertKey(Long eventId, Long personId, Comparable... values) {
        super(values);
        this.eventId = eventId;
        this.personId = personId;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getPersonId() {
        return personId;
    }
    
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.util;

import chronic.*;
import java.util.Comparator;

/**
 *
 * @author evan.summers
 */
public class StatusRecordDescendingTimestampComparator implements Comparator<StatusRecord> {

    @Override
    public int compare(StatusRecord o1, StatusRecord o2) {
        return Long.compare(o2.getTimestamp(), o1.getTimestamp());
    }
}

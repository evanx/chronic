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
public class AlertRecordDescendingTimestampComparator implements Comparator<AlertRecord> {

    @Override
    public int compare(AlertRecord o1, AlertRecord o2) {
        return Long.compare(o2.getStatus().getTimestamp(), o1.getStatus().getTimestamp());
    }
}

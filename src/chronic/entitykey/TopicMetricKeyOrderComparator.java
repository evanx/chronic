/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import java.util.Comparator;

/**
 *
 * @author evan.summers
 */
public final class TopicMetricKeyOrderComparator implements Comparator<TopicMetricKey> {

    @Override
    public int compare(TopicMetricKey o1, TopicMetricKey o2) {
        if (o1.compareTo(o2) == 0) {
            return Integer.compare(o1.getOrder(), o2.getOrder());
        }
        return o1.compareTo(o2);
    }
    
}

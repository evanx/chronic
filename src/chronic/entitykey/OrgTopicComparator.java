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
public final class OrgTopicComparator implements Comparator<OrgTopicKeyed> {

    @Override
    public int compare(OrgTopicKeyed o1, OrgTopicKeyed o2) {
        return o1.getOrgTopicKey().compareTo(o1.getOrgTopicKey());
    }

}

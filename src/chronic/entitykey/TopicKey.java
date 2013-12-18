/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import vellum.data.ComparableTuple;
import vellum.jx.JMap;
import vellum.jx.JMapException;

/**
 *
 * @author evan.summers
 */
public final class TopicKey extends ComparableTuple {
    String orgDomain;
    String orgUnit;
    String commonName;
    String topicString;

    public TopicKey(JMap map) throws JMapException {
        this(map.getString("orgDomain"), 
                map.getString("orgUnit"), 
                map.getString("commonName"),
                map.getString("topicString"));
    }
    
    public TopicKey(String orgDomain, String orgUnit, String commonName, String topicString) {
        super(orgDomain, orgUnit, commonName, topicString);
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
    }

    public TopicKey(CertKey certKey, String topicString) {
        this(certKey.getOrgDomain(), certKey.getOrgUnit(), certKey.getCommonName(), topicString);
    }

    public String getOrgDomain() {
        return orgDomain;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public String getCommonName() {
        return commonName;
    }
    
    public String getTopicString() {
        return topicString;
    }

    public JMap getMap() {
        JMap map = new JMap();
        map.put("orgDomain", orgDomain);
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        map.put("topicString", topicString);
        return map;
    }

}

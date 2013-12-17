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
    String orgUrl;
    String orgUnit;
    String commonName;
    String topicString;

    public TopicKey(JMap map) throws JMapException {
        this(map.getString("orgUrl"), 
                map.getString("orgUnit"), 
                map.getString("commonName"),
                map.getString("topicString"));
    }
    
    public TopicKey(String orgUrl, String orgUnit, String commonName, String topicString) {
        super(orgUrl, orgUnit, commonName, topicString);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.topicString = topicString;
    }

    public TopicKey(CertKey certKey, String topicString) {
        this(certKey.getOrgUrl(), certKey.getOrgUnit(), certKey.getCommonName(), topicString);
    }

    public String getOrgUrl() {
        return orgUrl;
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
        map.put("orgUrl", orgUrl);
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        map.put("topicString", topicString);
        return map;
    }

}

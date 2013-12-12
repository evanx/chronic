/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

/**
 *
 * @author evan.summers
 */
public class OrgKey {
    String orgUrl;

    public OrgKey(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        
}

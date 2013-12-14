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
public final class OrgUnitKey extends ComparableTuple {
    String orgUrl;
    String orgUnit;
    String commonName;
            
    public OrgUnitKey(String orgUrl, String orgUnit, String commonName) {
        super(orgUrl, orgUnit, commonName);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
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
}

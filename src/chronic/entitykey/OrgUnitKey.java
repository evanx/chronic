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
            
    public OrgUnitKey(String orgUrl, String orgUnit) {
        super(orgUrl, orgUnit);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getOrgUnit() {
        return orgUnit;
    }
}

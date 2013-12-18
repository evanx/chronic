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
    String orgDomain;
    String orgUnit;
            
    public OrgUnitKey(String orgDomain, String orgUnit) {
        super(orgDomain, orgUnit);
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
    }

    public String getOrgDomain() {
        return orgDomain;
    }

    public String getOrgUnit() {
        return orgUnit;
    }
}

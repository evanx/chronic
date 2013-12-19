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
public final class CertKey extends ComparableTuple {

    String orgDomain;
    String orgUnit;
    String commonName;

    public CertKey(JMap map) throws JMapException {
        this(map.getString("orgDomain"), 
                map.getString("orgUnit"), 
                map.getString("commonName"));
    }
    
    public CertKey(String orgDomain, String orgUnit, String commonName) {
        super(orgDomain, orgUnit, commonName);
        this.orgDomain = orgDomain;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
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
}

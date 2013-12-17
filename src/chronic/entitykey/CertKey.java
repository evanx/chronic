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

    String orgUrl;
    String orgUnit;
    String commonName;
    String address;

    public CertKey(JMap map) throws JMapException {
        this(map.getString("orgUrl"), 
                map.getString("orgUnit"), 
                map.getString("commonName"));
    }
    
    public CertKey(String orgUrl, String orgUnit, String commonName) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

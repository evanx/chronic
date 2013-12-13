/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public final class CertKey extends ComparableTuple {

    String orgUrl;
    String orgUnit;
    String commonName;
    String hostAddress;

    public CertKey(String orgUrl, String orgUnit, String commonName) {
        super(orgUrl, orgUnit, commonName);
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
    }
    

}

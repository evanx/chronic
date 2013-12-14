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
public class NetworkKey extends ComparableTuple {
    String orgUrl;
    String networkName;

    public NetworkKey(String orgUrl, String networkName) {
        super(orgUrl, networkName);        
        this.orgUrl = orgUrl;
        this.networkName = networkName;
    }

    public String getOrgUrl() {
        return orgUrl;
    }        

    public String getNetworkName() {
        return networkName;
    }        
}


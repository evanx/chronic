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
    String orgDomain;
    String networkName;

    public NetworkKey(String orgDomain, String networkName) {
        super(orgDomain, networkName);        
        this.orgDomain = orgDomain;
        this.networkName = networkName;
    }

    public String getOrgDomain() {
        return orgDomain;
    }        

    public String getNetworkName() {
        return networkName;
    }        
}


/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.NetworkKey;
import chronic.entitykey.NetworkKeyed;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.storage.AbstractEntity;
import vellum.type.Enabled;
import vellum.type.Labelled;

/**
 *
 * @author evan.summers
 */
public class Network extends AbstractEntity implements NetworkKeyed, Labelled, Enabled {
    static Logr logger = LogrFactory.getLogger(Network.class);
    
    String networkName;
    String label;
    String description;
    String address;
    String orgDomain;
    boolean enabled = true;
    
    public Network() {
    }

    public Network(NetworkKey key) {
        this.orgDomain = key.getOrgDomain();
        this.networkName = key.getNetworkName();
    }
    
    public Network(String orgName, String networkName) {
        this.orgDomain = orgName;
        this.networkName = networkName;
    }
    
    @Override
    public Comparable getKey() {
        return networkName;
    }

    @Override
    public NetworkKey getNetworkKey() {
        return new NetworkKey(orgDomain, networkName);
    }
    
    public String getNetworkName() {
        return networkName;
    }
    
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getOrgDomain() {
        return orgDomain;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
        
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

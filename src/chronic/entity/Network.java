/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.storage.AbstractEntity;

/**
 *
 * @author evan.summers
 */
public class Network extends AbstractEntity {
    static Logr logger = LogrFactory.getLogger(Network.class);
    
    String networkName;
    String label;
    String description;
    String address;
    String orgName;
    boolean enabled = true;
    
    public Network() {
    }

    public Network(String orgName, String name) {
        this.orgName = orgName;
        this.networkName = name;
    }
    
    @Override
    public Comparable getKey() {
        return networkName;
    }

    public String getNetworkName() {
        return networkName;
    }
    
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

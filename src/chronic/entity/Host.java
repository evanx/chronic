/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;
import vellum.storage.AbstractEntity;

/**
 *
 * @author evan.summers
 */
public class Host extends AbstractEntity {

    static Logr logger = LogrFactory.getLogger(Host.class);
    
    String name;
    String fullName;
    String ipNumber;
    boolean enabled;
    long orgId;
    transient Org org;    
    transient Network network;

    public Host() {
    }

    public Host(String name) {
        this.name = name;
    }

    @Override
    public Comparable getKey() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }
    
    public String getIpNumber() {
        return ipNumber;
    }

    public void setIpNumber(String ipNumber) {
        this.ipNumber = ipNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public Network getHostGroup() {
        return network;
    }

    public Network getNetwork() {
        return network;
    }
    
    @Override
    public String toString() {
        return Args.format(name);
    }
}

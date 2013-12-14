/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.CertKeyed;
import chronic.entitytype.CertAction;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public final class Cert extends AbstractIdEntity implements OrgKeyed, CertKeyed, Enabled {

    Long id;
    String orgUrl;
    String orgUnit;
    String commonName;
    String address;
    String encoded;
    boolean enabled;

    public Cert() {
    }

    public Cert(Long id, String orgUrl, String orgUnit, String commonName, String address) {
        this.id = id;
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
        this.address = address;
    }

    public Cert(CertKey key) {
        this.orgUrl = key.getOrgUrl();
        this.orgUnit = key.getOrgUnit();
        this.commonName = key.getCommonName();
    }
    
    public Cert(String orgUrl, String orgUnit, String commonName) {
        this.orgUrl = orgUrl;
        this.orgUnit = orgUnit;
        this.commonName = commonName;
    }

    @Override
    public Comparable getKey() {
        return Comparables.tuple(orgUrl, orgUnit, commonName);
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgUrl, orgUnit, commonName);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgUrl);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public String getOrgUrl() {
        return orgUrl;
    }

    public String getOrgUnit() {
        return orgUnit;
    }
    
    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgUrl", orgUrl);
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        map.put("enabled", enabled);
        map.put("address", address);
        map.put("actionLabel", getAction().getLabel());
        return map;
    }

    private CertAction getAction() {
        return enabled ? CertAction.DISABLE : CertAction.ENABLE;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

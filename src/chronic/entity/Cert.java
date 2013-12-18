/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entity;

import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.CertKeyed;
import chronic.entitykey.OrgUnitKey;
import chronic.entitykey.OrgUnitKeyed;
import chronic.entitytype.CertActionType;
import vellum.data.Millis;
import vellum.data.Timestamped;
import vellum.jx.JMap;
import vellum.storage.AbstractIdEntity;
import vellum.type.Enabled;

/**
 *
 * @author evan.summers
 */
public final class Cert extends AbstractIdEntity implements OrgKeyed, OrgUnitKeyed, 
        CertKeyed, Timestamped, Enabled {

    Long id;
    String orgUrl;
    String orgUnit;
    String commonName;
    String encoded;
    boolean enabled;
    
    transient long timestamp;
    transient String address;

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
        return getCertKey();
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
    public OrgUnitKey getOrgUnitKey() {
        return new OrgUnitKey(orgUrl, orgUnit);
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

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }
    
    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }
    
    public String getOrgUnit() {
        return orgUnit;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public String getCommonName() {
        return commonName;
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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
        
    public JMap getMap() {
        JMap map = new JMap();
        map.put("id", id);
        map.put("orgUrl", orgUrl);
        map.put("orgUnit", orgUnit);
        map.put("commonName", commonName);
        map.put("enabled", enabled);
        map.put("address", address);
        map.put("timestampLabel", Millis.formatTime(timestamp));
        map.put("action", getAction());
        map.put("actionLabel", getAction().getLabel());
        return map;
    }

    private CertActionType getAction() {
        return enabled ? CertActionType.REVOKE : CertActionType.GRANT;
    }
    
    @Override
    public String toString() {
        return getMap().toString();
    }
}

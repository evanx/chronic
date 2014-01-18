/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitykey;

import java.security.cert.CertificateException;
import vellum.data.ComparableTuple;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.security.Certificates;
import vellum.security.DnameType;

/**
 *
 * @author evan.summers
 */
public final class CertKey extends ComparableTuple {

    String orgDomain;
    String orgUnit;
    String commonName;

    public CertKey(String subject) throws CertificateException {        
        this(Certificates.get(DnameType.O, subject), 
                Certificates.get(DnameType.OU, subject), 
                Certificates.get(DnameType.CN, subject));
    }
    
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

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.storage;

import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.entity.AbstractIdEntity;
import vellum.parameter.StringMap;
import vellum.security.Certificates;
import vellum.security.PemCerts;

/**
 *
 * @author evan.summers
 */
public class AdminUser extends AbstractIdEntity<Long> {
    Long id;
    String userName;
    String displayName;
    String firstName;
    String lastName;
    long authMillis;
    boolean enabled = true;
    String email;
    Date inserted;
    Date updated;
    String secret;
    String cert;
    String locality;
    String region;
    String country;
    String createdBy;
    String secondedBy;
    String passwordHash;
    String passwordSalt;
    Date loginTime;
    Date logoutTime;
    String subject;
    
    public AdminUser() {
    }

    public AdminUser(String userName) {
        this.userName = userName;
    }
    
    public AdminUser(String userName, String displayName, boolean enabled) {
        this.userName = userName;
        this.displayName = displayName;
        this.enabled = enabled;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String formatSubject() {
        return Certificates.formatDname(email, displayName, userName, locality, region, country);
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
   
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
   
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getSecondedBy() {
        return secondedBy;
    }

    public void setSecondedBy(String secondedBy) {
        this.secondedBy = secondedBy;
    }

    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCert(X509Certificate x509Cert) {
        this.cert = PemCerts.buildCertPem(x509Cert);
        this.subject = x509Cert.getSubjectDN().getName();
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAuthMillis(long authMillis) {
        this.authMillis = authMillis;
    }

    public long getAuthMillis() {
        return authMillis;
    }

    public StringMap getStringMap() {
        StringMap map = new StringMap();
        map.put("id", id);
        map.put("userName", userName);
        map.put("displayName", displayName);
        map.put("region", region);
        map.put("locality", locality);
        map.put("country", country);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public String toString() {
        return getStringMap().toJson();
    }
    
}

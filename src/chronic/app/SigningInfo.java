/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 *
 * @author evan.summers
 */
public class SigningInfo {
    
    int validityDays;
    PrivateKey signingKey;
    X509Certificate signingCert;

    public SigningInfo(int validityDays, PrivateKey signingKey, X509Certificate signingCert) {
        this.validityDays = validityDays;
        this.signingKey = signingKey;
        this.signingCert = signingCert;
    }

    public int getValidityDays() {
        return validityDays;
    }        
    
    public X509Certificate getSigningCert() {
        return signingCert;
    }

    public PrivateKey getSigningKey() {
        return signingKey;
    }        
}

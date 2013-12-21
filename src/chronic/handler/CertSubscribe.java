/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Cert;
import chronic.transaction.EnrollCertSubscriberTransaction;
import chronic.transaction.EnrollCertTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class CertSubscribe implements ChronicHttpxHandler {
    
    static Logger logger = LoggerFactory.getLogger(CertSubscribe.class);

    @Override
    public JMap handle(ChronicHttpx hx) throws Exception {
        String email = hx.readString().trim();
        Cert cert = new EnrollCertTransaction().handle(hx);
        if (!Emails.matchesEmail(email)) {
            return new JMap(String.format("ERROR: invalid email %s", email));
        } else {
            new EnrollCertSubscriberTransaction().handle(hx, cert, email);
            return new JMap(String.format("OK: %s %s\n", cert.getCommonName(), email));
        }
    }
}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.entity.Cert;
import chronic.transaction.EnrollCertSubscriberTransaction;
import chronic.transaction.EnrollCertTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;

/**
 *
 * @author evan.summers
 */
public class CertSubscribe {
    
    static Logger logger = LoggerFactory.getLogger(CertSubscribe.class);

    public void handle(ChronicHttpx hx) throws Exception {
        String email = hx.readString().trim();
        Cert cert = new EnrollCertTransaction().handle(hx);
        if (Emails.matchesEmail(email)) {
            new EnrollCertSubscriberTransaction().handle(hx, cert, email);
            hx.sendPlainResponse("ok %s %s", cert.getCommonName(), email);
        } else {
            hx.sendError("invalid email %s", email);
        }
        hx.close();
    }
}

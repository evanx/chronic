/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Cert;
import chronic.transaction.EnrollCertSubscriberTransaction;
import chronic.transaction.EnrollCertTransaction;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.enumtype.DelimiterType;
import vellum.jx.JMap;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class CertSubscribe implements ChronicHttpxHandler {
    
    static Logger logger = LoggerFactory.getLogger(CertSubscribe.class);

    @Override
    public JMap handle(ChronicHttpx hx) throws Exception {
        Cert cert = new EnrollCertTransaction().handle(hx);
        String[] emails = Strings.split(hx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (!Emails.matchesEmail(email)) {
                return new JMap(String.format("ERROR: invalid email: %s\n", email));
            } else {
                new EnrollCertSubscriberTransaction().handle(hx, cert, email);
            }
        }
        return new JMap(String.format("OK: %s: %s\n", cert.getCommonName(), Arrays.toString(emails)));
    }
}

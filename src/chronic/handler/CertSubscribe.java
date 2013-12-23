/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.entity.Cert;
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
    public JMap handle(ChronicApp app, ChronicHttpx hx) throws Exception {
        Cert cert = hx.persistCert();
        String[] emails = Strings.split(hx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (!Emails.matchesEmail(email)) {
                return new JMap(String.format("ERROR: invalid email: %s\n", email));
            } else {
                hx.persistCertSubscriber(cert, email);
            }
        }
        return new JMap(String.format("OK: %s: %s\n", cert.getCommonName(), Arrays.toString(emails)));
    }
}

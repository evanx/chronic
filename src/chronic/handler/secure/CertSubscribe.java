/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.secure;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.enumtype.DelimiterType;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class CertSubscribe implements ChronicPlainHttpxHandler {
    
    static Logger logger = LoggerFactory.getLogger(CertSubscribe.class);

    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        Cert cert = es.persistCert(httpx);
        String[] emails = Strings.split(httpx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (!Emails.matchesEmail(email)) {
                return String.format("ERROR: invalid email: %s\n", email);
            } else {
                es.persistCertSubscription(cert, email);
            }
        }
        return String.format("OK: %s\n", Arrays.toString(emails));
    }
}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Cert;
import chronic.entitytype.OrgRoleType;
import chronic.transaction.EnrollRoleTransaction;
import chronic.transaction.EnrollCertTransaction;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.enumtype.DelimiterType;
import vellum.exception.DisplayException;
import vellum.jx.JMap;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AdminEnroll implements ChronicHttpxHandler {
    
    static Logger logger = LoggerFactory.getLogger(AdminEnroll.class);
    
    @Override
    public JMap handle(ChronicHttpx hx) throws Exception {
        Cert cert = new EnrollCertTransaction().handle(hx);
        String[] emails = Strings.split(hx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (!Emails.matchesEmail(email)) {
                return new JMap(String.format("ERROR: invalid email %s", email));
            } else {
                new EnrollRoleTransaction().handle(hx, cert, email, OrgRoleType.ADMIN);
            }
        }
        return new JMap(String.format("OK: %s %s", cert.getOrgDomain(), Arrays.toString(emails)));
    }
}

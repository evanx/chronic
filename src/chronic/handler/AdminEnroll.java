/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import chronic.entitytype.OrgRoleType;
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
public class AdminEnroll implements ChronicPlainHttpxHandler {
    
    static Logger logger = LoggerFactory.getLogger(AdminEnroll.class);
    
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) throws Exception {
        Cert cert = es.persistCert(httpx);
        String[] emails = Strings.split(httpx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (!Emails.matchesEmail(email)) {
                return "ERROR: invalid email: " + email;
            } else {
                es.persistOrgRole(cert.getOrg(), email, OrgRoleType.ADMIN);
            }
        }
        return "OK: " + Arrays.toString(emails);
    }
}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.transaction.EnrollTransaction;
import chronic.*;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.enumtype.DelimiterType;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Enroll {
    
    static Logger logger = LoggerFactory.getLogger(Enroll.class);
    ChronicApp app;
    Httpx hx;
    
    public Enroll(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        hx = new Httpx(httpExchange);
        String orgUrl = Certificates.getOrg(hx.getSSLSession().getPeerPrincipal());
        String[] emails = Strings.split(hx.readString(), DelimiterType.COMMA_OR_SPACE);
        for (String email : emails) {
            if (Emails.matchesEmail(email)) {
                new EnrollTransaction().handle(app, orgUrl, email);
            } else {
                hx.handleError("invalid email %s", email);
            }
        }
        hx.sendPlainResponse("ok %s %s", orgUrl, emails);
        hx.close();
    }
}

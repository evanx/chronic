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
import vellum.httpserver.Httpx;
import vellum.security.Certificates;

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
        String email = hx.readString().trim();
        if (Emails.matchesEmail(email)) {            
            String orgUrl = Certificates.getOrg(hx.getSSLSession().getPeerPrincipal());
            new EnrollTransaction().handle(app, orgUrl, email);
            hx.sendPlainResponse("ok %s %s", orgUrl, email);
        } else {
            hx.handleError("invalid email %s", email);
        }
        hx.close();
    }
}

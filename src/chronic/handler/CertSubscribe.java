/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
import chronic.transaction.EnrollCertSubscriberTransaction;
import chronic.transaction.EnrollCertTransaction;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Emails;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class CertSubscribe {
    
    static Logger logger = LoggerFactory.getLogger(CertSubscribe.class);
    ChronicApp app;
    Httpx hx;
    
    public CertSubscribe(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        hx = new Httpx(httpExchange);
        String email = hx.readString().trim();
        Cert cert = new EnrollCertTransaction().handle(app, 
                hx.getRemoteHostAddress(), hx.getPeerCertficate());
        if (Emails.matchesEmail(email)) {
            new EnrollCertSubscriberTransaction().handle(app, cert, email);
            hx.sendPlainResponse("ok %s %s", cert.getCommonName(), email);
        } else {
            hx.sendError("invalid email %s", email);
        }
        hx.close();
    }
}

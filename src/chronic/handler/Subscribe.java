/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Emails;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;

/**
 *
 * @author evan.summers
 */
public class Subscribe {
    
    static Logger logger = LoggerFactory.getLogger(Subscribe.class);
    ChronicApp app;
    Httpx hx;
    
    public Subscribe(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        hx = new Httpx(httpExchange);
        String email = hx.readString().trim();
        if (Emails.matchesEmail(email)) {
            String orgName = Certificates.getOrg(hx.getSSLSession().getPeerPrincipal());
            app.getStorage().subscribe(email, orgName);
            hx.sendPlainResponse("ok %s %s", email, orgName);
        } else {
            hx.handleError("invalid email %s", email);
        }
        hx.close();
    }

}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.transaction.SubscribeTransaction;
import chronic.*;
import chronic.entity.AdminUser;
import chronic.entity.AdminUserRoleType;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Emails;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;
import vellum.storage.StorageException;
import vellum.type.ComparableTuple;
import vellum.util.Comparables;

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
            String orgUrl = Certificates.getOrg(hx.getSSLSession().getPeerPrincipal());
            new SubscribeTransaction().handle(app, orgUrl, email);
            hx.sendPlainResponse("ok %s %s", orgUrl, email);
        } else {
            hx.handleError("invalid email %s", email);
        }
        hx.close();
    }
}

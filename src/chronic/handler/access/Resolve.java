/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.app.ChronicApp;
import chronic.entity.OrgRole;
import chronic.entitytype.OrgRoleType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.enumtype.DelimiterType;
import vellum.httpserver.Httpx;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Resolve implements HttpHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Resolve.class);
 
    ChronicApp app;
    
    public Resolve(ChronicApp app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        Httpx httpx = new Httpx(he);
        String orgDomain = httpx.readString();
        String server = app.getServer(orgDomain);
        for (String adminEmail : Strings.split(httpx.getRequestHeader("Admin"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("admin: {}", adminEmail);
            app.add(new OrgRole(orgDomain, adminEmail, OrgRoleType.ADMIN));
        }
        for (String subscriberEmail : Strings.split(httpx.getRequestHeader("Subscribe"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("subscriber: {}", subscriberEmail);
            app.add(new OrgRole(orgDomain, subscriberEmail, OrgRoleType.SUBSCRIBER));
        }
        if (server == null) {
            logger.warn("not found: {}", orgDomain);
            httpx.sendPlainResponse("secure.chronica.co");
        } else {
            httpx.sendPlainResponse(server);
        }
        httpx.close();                
    }
}

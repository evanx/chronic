/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.app.ChronicApp;
import chronic.entitykey.OrgRoleKey;
import chronic.entitytype.OrgRoleType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
        String orgDomain = httpx.readString().trim();
        String server = app.getResolvedServer(orgDomain);
        for (String adminEmail : Strings.split(httpx.getRequestHeader("Admin"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("admin: {}", adminEmail);
            app.getOrgRoleQueue().add(new OrgRoleKey(orgDomain, adminEmail, OrgRoleType.ADMIN));
        }
        for (String subscriberEmail : Strings.split(httpx.getRequestHeader("Subscribe"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("subscriber: {}", subscriberEmail);
            app.getOrgRoleQueue().add(new OrgRoleKey(orgDomain, subscriberEmail, OrgRoleType.SUBSCRIBER));
        }
        if (server == null) {
            logger.warn("not found: {}", orgDomain);
            server = "secure.chronica.co";
        }
        logger.info("server {}", server);
        int port = 443;
        if (server.equals("localhost")) {
            port = 8444;
        }
        httpx.sendPlainResponse(String.format("%s:%d\n", server, port));
        httpx.close();                
    }
}

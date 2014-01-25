/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.app.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.enumtype.DelimiterType;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Resolve implements PlainHttpxHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Resolve.class);
 
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        Org org = cert.getOrg();
        for (String adminEmail : Strings.split(httpx.getRequestHeader("Admin"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("admin: {}", adminEmail);
            es.persistOrgRole(org, adminEmail, OrgRoleType.ADMIN);
        }
        for (String subscriberEmail : Strings.split(httpx.getRequestHeader("Subscribe"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("subscriber: {}", subscriberEmail);
            es.persistOrgRole(org, subscriberEmail, OrgRoleType.SUBSCRIBER);
        }
        int port = 443;
        if (org.getServer().equals("localhost")) {
            port = 8444;
        }
        String address = String.format("%s:%d", org.getServer(), port);
        logger.info("server address {}", address);
        return address;
    }
}

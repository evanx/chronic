/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class RoleList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleList.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        Set roles = new TreeSet();
        for (OrgRole role : app.storage().listRoles(app.getEmail(httpx))) {
            roles.add(role.getMap());
        }
        if (roles.isEmpty() && app.getProperties().isDemo(httpx.getServerUrl())) {
            String adminEmail = app.getProperties().getAdminEmails().iterator().next();
            for (OrgRole role : app.storage().listRoles(adminEmail)) {
                roles.add(role.getMap());
            }
        }
        return JMaps.create("roles", roles);
    }
    
}

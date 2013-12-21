/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class RoleList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleList.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        List roles = new LinkedList();
        for (OrgRole role : httpx.db.listRoles(httpx.app.getEmail(httpx))) {
            roles.add(role.getMap());
        }
        if (roles.isEmpty() && httpx.app.getProperties().isDemo(httpx)) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (OrgRole role : httpx.db.listRoles(adminEmail)) {
                roles.add(role.getMap());
            }
        }
        return JMaps.mapValue("roles", roles);
    }
    
}

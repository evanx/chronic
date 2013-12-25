/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        List roles = new LinkedList();
        for (OrgRole role : es.listRoles(httpx.getEmail())) {
            roles.add(role.getMap());
        }
        if (roles.isEmpty() && httpx.getReferer().endsWith("/demo")) {
            String adminEmail = httpx.app.getProperties().getAdminEmails().iterator().next();
            for (OrgRole role : es.listRoles(adminEmail)) {
                roles.add(role.getMap());
            }
        }
        return JMaps.mapValue("roles", roles);
    }
    
}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import java.util.LinkedList;
import java.util.List;
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
        List roles = new LinkedList();
        for (OrgRole role : app.storage().listRoles(app.getEmail(httpx))) {
            roles.add(role.getMap());
        }
        return JMaps.create("roles", roles);
    }
    
}
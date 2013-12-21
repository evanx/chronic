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
public class RoleActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleActionAll.class);
  
    ChronicHttpx httpx;
    String email;
    
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        this.httpx = httpx;
        email = httpx.app.getEmail(httpx);
        List roles = new LinkedList();
        for (OrgRole role : httpx.db.listRoles(httpx.app.getEmail(httpx))) {
            if (!role.isEnabled()) {
                role.setEnabled(true);
                httpx.db.role().replace(role);
            }
            roles.add(role.getMap());
        }
        return JMaps.mapValue("roles", roles);
    }
    
}

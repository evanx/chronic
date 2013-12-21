/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import chronic.entitykey.OrgRoleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class RoleAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleAction.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        OrgRoleKey roleKey = new OrgRoleKey(httpx.parseJsonMap().getMap("role"), email);
        if (!httpx.db.role().contains(roleKey)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            OrgRole orgRole = httpx.db.role().find(roleKey);
            orgRole.setEnabled(!orgRole.isEnabled());
            httpx.db.role().replace(orgRole);
            return JMaps.mapValue("role", orgRole.getMap());
        }
    }
    
}

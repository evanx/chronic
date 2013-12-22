/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
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
        String email = httpx.getEmail();
        OrgRoleKey roleKey = new OrgRoleKey(httpx.parseJsonMap().getMap("role"), email);
        if (!httpx.db.role().retrievable(roleKey)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            OrgRole orgRole = httpx.db.role().find(roleKey);
            orgRole.setEnabled(!orgRole.isEnabled());
            httpx.db.role().update(orgRole);
            return JMaps.mapValue("role", orgRole.getMap());
        }
    }
    
}

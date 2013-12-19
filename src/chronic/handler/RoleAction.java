/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import chronic.entitykey.OrgRoleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class RoleAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleAction.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        OrgRoleKey roleKey = new OrgRoleKey(httpx.parseJsonMap().getMap("role"), email);
        if (!app.storage().role().containsKey(roleKey)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            OrgRole orgRole = app.storage().role().select(roleKey);
            orgRole.setEnabled(!orgRole.isEnabled());
            app.storage().role().update(orgRole);
            return JMaps.mapValue("role", orgRole.getMap());
        }
    }
    
}

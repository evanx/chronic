/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.web;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        OrgRoleKey roleKey = new OrgRoleKey(httpx.parseJsonMap().getMap("role"));
        OrgRole orgRole = es.findOrgRole(roleKey);
        if (orgRole == null) {
            throw new Exception("no such role: " + roleKey);
        }
        String email = httpx.getEmail();
        if (es.isAdmin(orgRole.getOrgDomain(), email)) {
            orgRole.setEnabled(!orgRole.isEnabled());
        } else {
            logger.warn("not admin {} {}", email, roleKey);
        }
        return JMaps.mapValue("role", orgRole.getMap());
    }

}

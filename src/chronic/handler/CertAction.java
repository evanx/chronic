/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class CertAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertAction.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        CertKey certKey = new CertKey(httpx.parseJsonMap().getMap("cert"));
        OrgRoleKey roleKey = new OrgRoleKey(certKey.getOrgUrl(), email, OrgRoleType.ADMIN);
        if (!app.storage().role().containsKey(roleKey)) {
            return JMaps.create("errorMessage", "no role");
        } else {
            Cert cert = app.storage().cert().select(certKey);
            cert.setEnabled(!cert.isEnabled());
            app.storage().cert().update(cert);
            return JMaps.create("cert", cert.getMap());
        }
    }
    
}

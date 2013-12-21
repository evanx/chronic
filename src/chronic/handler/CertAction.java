/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class CertAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertAction.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        CertKey certKey = new CertKey(httpx.parseJsonMap().getMap("cert"));
        OrgRoleKey roleKey = new OrgRoleKey(certKey.getOrgDomain(), email, OrgRoleType.ADMIN);
        if (!httpx.db.role().contains(roleKey)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            Cert cert = httpx.db.cert().find(certKey);
            cert.setEnabled(!cert.isEnabled());
            httpx.db.cert().replace(cert);
            return JMaps.mapValue("cert", cert.getMap());
        }
    }
    
}

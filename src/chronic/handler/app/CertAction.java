/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.app;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        CertKey certKey = new CertKey(httpx.parseJsonMap().getMap("cert"));
        if (!es.isAdmin(certKey.getOrgDomain(), email)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            Cert cert = es.findCert(certKey);
            cert.setEnabled(!cert.isEnabled());
            return JMaps.mapValue("cert", cert.getMap());
        }
    }
    
}

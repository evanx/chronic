/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Cert;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class CertList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertList.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        Set certs = new TreeSet();
        for (Cert cert : app.storage().listCerts(email)) {
            certs.add(cert);
        }
        if (certs.isEmpty() && app.getProperties().isDemo(httpx)) {
            String adminEmail = app.getProperties().getAdminEmail();
            for (Cert cert : app.storage().listCerts(adminEmail)) {
                certs.add(cert);
            }
        } 
        return JMaps.map("certs", certs);
    }
    
}

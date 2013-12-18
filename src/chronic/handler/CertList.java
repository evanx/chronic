/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Cert;
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
public class CertList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertList.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        List certs = new LinkedList();
        for (Cert cert : app.storage().listCerts(email)) {
            certs.add(cert.getMap(false));
        }
        if (certs.isEmpty() && app.getProperties().isDemo(httpx.getServerUrl())) {
            String adminEmail = app.getProperties().getAdminEmails().iterator().next();
            for (Cert cert : app.storage().listCerts(adminEmail)) {
                certs.add(cert.getMap(false));
            }
        } 
        return JMaps.create("certs", certs);
    }
    
}

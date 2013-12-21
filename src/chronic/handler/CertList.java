/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Cert;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class CertList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertList.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.app.getEmail(httpx);
        Set certs = new TreeSet();
        for (Cert cert : httpx.db.listCerts(email)) {
            certs.add(cert);
        }
        if (certs.isEmpty() && httpx.app.getProperties().isDemo(httpx)) {
            String adminEmail = httpx.app.getProperties().getAdminEmail();
            for (Cert cert : httpx.db.listCerts(adminEmail)) {
                certs.add(cert);
            }
        } 
        return JMaps.map("certs", certs);
    }
    
}

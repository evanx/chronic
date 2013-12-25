/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
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
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        Set certs = new TreeSet();
        for (Cert cert : es.listCerts(email)) {
            certs.add(cert);
        }
        if (certs.isEmpty() && httpx.getReferer().endsWith("/demo")) {
            String adminEmail = httpx.app.getProperties().getAdminEmail();
            for (Cert cert : es.listCerts(adminEmail)) {
                certs.add(cert);
            }
        } 
        logger.info("certs {}", certs.size());
        return JMaps.map("certs", certs);
    }
    
}

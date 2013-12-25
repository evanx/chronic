/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class CertActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertActionNone.class);
  
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        List certs = new LinkedList();
        for (Cert cert : es.listCerts(httpx.getEmail())) {
            if (cert.isEnabled()) {
                cert.setEnabled(false);
            }
        }
        return JMaps.map("certs", certs);
    }
    
}

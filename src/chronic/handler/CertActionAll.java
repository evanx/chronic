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
public class CertActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertActionAll.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        List certs = new LinkedList();
        for (Cert cert : app.storage().listCerts(app.getEmail(httpx))) {
            cert.setEnabled(true);
            app.storage().cert().update(cert);
        }
        return JMaps.create("certs", certs);
    }
    
}

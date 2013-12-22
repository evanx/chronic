/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
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
public class CertActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertActionAll.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        List certs = new LinkedList();
        for (Cert cert : httpx.db.listCerts(httpx.getEmail())) {
            cert.setEnabled(true);
            httpx.db.cert().update(cert);            
            certs.add(cert);
        }
        return JMaps.map("certs", certs);
    }
    
}

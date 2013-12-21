/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
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
public class CertActionNone implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(CertActionNone.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        List certs = new LinkedList();
        for (Cert cert : httpx.db.listCerts(httpx.app.getEmail(httpx))) {
            cert.setEnabled(false);
            httpx.db.cert().replace(cert);
        }
        return JMaps.map("certs", certs);
    }
    
}

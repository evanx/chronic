/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.ChronicHandler;
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
public class ListCerts implements ChronicHandler {

    Logger logger = LoggerFactory.getLogger(ListCerts.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        List certs = new LinkedList();
        for (Cert cert : app.getStorage().listCerts(app.getVerifiedEmail(httpx))) {
            certs.add(cert.getMap());
        }
        return JMaps.create("certs", certs);
    }
    
}

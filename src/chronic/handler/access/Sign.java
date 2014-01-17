/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.app.ChronicHttpx;
import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class Sign implements PlainHttpxHandler {
    
    Logger logger = LoggerFactory.getLogger(Sign.class);
 
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        String certReq = httpx.readString();
        logger.info("certReq {}", certReq);
        return "OK\n";
    }

}

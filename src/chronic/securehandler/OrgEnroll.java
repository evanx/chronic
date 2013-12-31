/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.securehandler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class OrgEnroll implements ChronicHttpxHandler {
    
    Logger logger = LoggerFactory.getLogger(OrgEnroll.class);
 
    
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        return new JMap();
    }

}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class EnrollOrg implements ChronicHandler {
    
    Logger logger = LoggerFactory.getLogger(EnrollOrg.class);
 
    
    @Override
    public JMap handle(ChronicApp app, Httpx hx) throws Exception {
        return new JMap();
    }

}

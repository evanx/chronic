/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class EnrollOrg implements ChronicHttpxHandler {
    
    Logger logger = LoggerFactory.getLogger(EnrollOrg.class);
 
    
    @Override
    public JMap handle(ChronicHttpx hx) throws Exception {
        return new JMap();
    }

}

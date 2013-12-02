/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.entity.Org;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class EnrollOrg {
    
    Logger logger = LoggerFactory.getLogger(EnrollOrg.class);
    ChronicApp app;
    Httpx hx;
 
    public EnrollOrg(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(Httpx hx) throws Exception {
        this.hx = hx;
        Org org = new Org();

    }

}

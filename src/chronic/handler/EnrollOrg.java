/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.entity.Org;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class EnrollOrg {
    
    Logger logger = LoggerFactory.getLogger(EnrollOrg.class);
    ChronicApp app;
    
    public EnrollOrg(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        Org org = new Org();

    }

}

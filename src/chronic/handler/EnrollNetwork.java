/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.entity.Network;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class EnrollNetwork {
    
    Logger logger = LoggerFactory.getLogger(EnrollNetwork.class);
    ChronicApp app;
    
    public EnrollNetwork(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        Network network = new Network();
    }

}

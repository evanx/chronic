/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.*;
import chronic.entity.AdminUser;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class EnrollAdminUser {
    
    Logger logger = LoggerFactory.getLogger(EnrollAdminUser.class);
    ChronicApp app;
    Httpx hx;
    
    public EnrollAdminUser(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(HttpExchange httpExchange) throws Exception {
        hx = new Httpx(httpExchange);
        JMap map = hx.parseJsonMap();
        AdminUser adminUser = new AdminUser(map);
    }

}

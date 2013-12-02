/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic;

import chronic.handler.EnrollAdminUser;
import chronic.handler.EnrollNetwork;
import chronic.handler.EnrollOrg;
import chronic.handler.ListAlerts;
import chronic.handler.PostHttpHandler;
import chronic.persona.LoginPersona;
import chronic.persona.LogoutPersona;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httphandler.WebHttpHandler;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpHandler implements HttpHandler {
    private final static Logger logger = LoggerFactory.getLogger(ChronicHttpHandler.class);
    private final static WebHttpHandler webHandler = new WebHttpHandler("/chronic/web");
    
    ChronicApp app;
    
    public ChronicHttpHandler(ChronicApp app) {
        this.app = app;
    }
    
    @Override
    public synchronized void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            logger.info("handle {}", path);
            if (path.equals("/post")) {                
                new PostHttpHandler(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollAdminUser")) {
                new EnrollAdminUser(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollNetwork")) {
                new EnrollNetwork(app).handle(httpExchange);
            } else if (path.equals("/app/ListAlerts")) {
                new ListAlerts(app).handle(httpExchange);
            } else if (path.equals("/app/LoginPersona")) {
                new LoginPersona(app).handle(httpExchange);
            } else if (path.equals("/app/LogoutPersona")) {
                new LogoutPersona(app).handle(httpExchange);
            } else if (path.startsWith("/app/")) {
                Httpx hx = new Httpx(httpExchange);
                if (path.equals("/app/EnrollOrg")) {
                    new EnrollOrg(app).handle(hx);
                } else {
                    logger.warn("Invalid request handler {}", path);
                    hx.handleError("Invalid request handler");
                    hx.close();
                }
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}

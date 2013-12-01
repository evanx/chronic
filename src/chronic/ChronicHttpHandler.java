/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic;

import chronic.handler.EnrollAdminUser;
import chronic.handler.EnrollNetwork;
import chronic.handler.EnrollOrg;
import chronic.post.PostHttpHandler;
import chronic.webauth.persona.LoginPersona;
import chronic.webauth.persona.LogoutPersona;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httphandler.WebHttpHandler;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpHandler implements HttpHandler {
    private final static int contentLengthLimit = 4000;    
    private final static Logger logger = LoggerFactory.getLogger(ChronicHttpHandler.class);
    private final static WebHttpHandler webHandler = new WebHttpHandler("/chronic/web");
    
    ChronicApp app;
    
    public ChronicHttpHandler(ChronicApp app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.trace("path {}", path);
        try {
            if (path.equals("/post")) {                
                new PostHttpHandler(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollAdminUser")) {
                new EnrollAdminUser(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollOrg")) {
                new EnrollOrg(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollNetwork")) {
                new EnrollNetwork(app).handle(httpExchange);
            } else if (path.equals("/app/LoginPersona")) {
                new LoginPersona(app).handle(httpExchange);
            } else if (path.equals("/app/LogoutPersona")) {
                new LogoutPersona(app).handle(httpExchange);
            } else if (path.startsWith("/app/")) {
                logger.warn("Invalid request handler {}", path);
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_IMPLEMENTED, 0);
                httpExchange.getResponseBody().write(0);
                httpExchange.close();
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}

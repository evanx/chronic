/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic;

import chronic.handler.Enroll;
import chronic.handler.EnrollOrg;
import chronic.handler.ListAlerts;
import chronic.handler.ListTopics;
import chronic.handler.Post;
import chronic.handler.Subscribe;
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
            logger.trace("handle {}", path);
            if (path.equals("/post")) {                
                new Post(app).handle(httpExchange);
            } else if (path.equals("/enroll")) {
                new Enroll(app).handle(httpExchange);
            } else if (path.equals("/subscribe")) {
                new Subscribe(app).handle(httpExchange);
            } else if (path.equals("/app/EnrollOrg")) {
                handle(new EnrollOrg(), new Httpx(httpExchange));
            } else if (path.equals("/app/ListAlerts")) {
                handle(new ListAlerts(), new Httpx(httpExchange));
            } else if (path.equals("/app/ListTopics")) {
                handle(new ListTopics(), new Httpx(httpExchange));
            } else if (path.equals("/app/LoginPersona")) {
                new LoginPersona(app).handle(httpExchange);
            } else if (path.equals("/app/LogoutPersona")) {
                new LogoutPersona(app).handle(httpExchange);
            } else if (path.startsWith("/app/")) {
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private void handle(ChronicHandler handler, Httpx httpx) {
        try {
            httpx.sendResponse(handler.handle(app, httpx));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            httpx.handleError(e);
        }
        httpx.close();
    }

}

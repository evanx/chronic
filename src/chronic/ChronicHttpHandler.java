/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic;

import chronic.handler.Enroll;
import chronic.handler.EnrollOrg;
import chronic.handler.ListAlerts;
import chronic.handler.ListCerts;
import chronic.handler.ListSubscribers;
import chronic.handler.ListTopics;
import chronic.handler.ListRoles;
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
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            logger.trace("handle {}", path);
            if (path.equals("/post")) {
                new Post(app).handle(httpExchange);
            } else if (path.equals("/enroll")) {
                new Enroll(app).handle(httpExchange);
            } else if (path.equals("/subscribe")) {
                new Subscribe(app).handle(httpExchange);
            } else if (path.startsWith("/chronicapp/")) {
                logger.info("path {}", path);
                if (path.equals("/chronicapp/personaLogin")) {
                    new LoginPersona(app).handle(httpExchange);
                } else if (path.equals("/chronicapp/personaLogout")) {
                    new LogoutPersona(app).handle(httpExchange);
                } else if (path.equals("/chronicapp/orgEnroll")) {
                    handle(new EnrollOrg(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/alertList")) {
                    handle(new ListAlerts(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/topicList")) {
                    handle(new ListTopics(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/subscriberList")) {
                    handle(new ListSubscribers(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/roleList")) {
                    handle(new ListRoles(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/certList")) {
                    handle(new ListCerts(), new Httpx(httpExchange));
                } else {
                    logger.warn("handle {}", path);
                }
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            
        }
    }

    private void handle(ChronicHttpxHandler handler, Httpx httpx) {
        try {
            httpx.sendResponse(handler.handle(app, httpx));
        } catch (Exception e) {
            httpx.handleError(e);
        }
        httpx.close();
    }

}

/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.handler.CertAction;
import chronic.handler.CertActionAll;
import chronic.handler.CertActionNone;
import chronic.handler.AdminEnroll;
import chronic.handler.EnrollOrg;
import chronic.handler.AlertList;
import chronic.handler.CertList;
import chronic.handler.SubscriberList;
import chronic.handler.TopicList;
import chronic.handler.RoleList;
import chronic.handler.Post;
import chronic.handler.RoleAction;
import chronic.handler.RoleActionAll;
import chronic.handler.CertSubscribe;
import chronic.handler.SubscriberAction;
import chronic.handler.SubscriberActionAll;
import chronic.handler.SubscriberActionNone;
import chronic.handler.TopicAction;
import chronic.handler.TopicActionAll;
import chronic.handler.TopicActionNone;
import chronic.persona.PersonaLogin;
import chronic.persona.PersonaLogout;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
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
        String path = httpExchange.getRequestURI().getPath();
        logger.trace("handle {} {}", path, httpExchange.getRequestURI().getHost());
        try {
            if (path.equals("/post")) {
                new Post(app).handle(httpExchange);
            } else if (path.equals("/enroll")) {
                new AdminEnroll(app).handle(httpExchange);
            } else if (path.equals("/subscribe")) {
                new CertSubscribe(app).handle(httpExchange);
            } else if (path.startsWith("/chronicapp/")) {
                logger.info("path {}", path);
                if (path.equals("/chronicapp/personaLogin")) {
                    new PersonaLogin(app).handle(httpExchange);
                } else if (path.equals("/chronicapp/personaLogout")) {
                    new PersonaLogout(app).handle(httpExchange);
                } else if (path.equals("/chronicapp/orgEnroll")) {
                    handle(new EnrollOrg(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/alertList")) {
                    handle(new AlertList(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/topicList")) {
                    handle(new TopicList(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/topicActionAll")) {
                    handle(new TopicActionAll(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/topicActionNone")) {
                    handle(new TopicActionNone(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/topicAction")) {
                    handle(new TopicAction(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/subscriberList")) {
                    handle(new SubscriberList(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/subscriberActionAll")) {
                    handle(new SubscriberActionAll(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/subscriberActionNone")) {
                    handle(new SubscriberActionNone(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/subscriberAction")) {
                    handle(new SubscriberAction(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/roleList")) {
                    handle(new RoleList(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/roleActionAll")) {
                    handle(new RoleActionAll(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/roleAction")) {
                    handle(new RoleAction(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/certList")) {
                    handle(new CertList(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/certAction")) {
                    handle(new CertAction(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/certActionAll")) {
                    handle(new CertActionAll(), new Httpx(httpExchange));
                } else if (path.equals("/chronicapp/certActionNone")) {
                    handle(new CertActionNone(), new Httpx(httpExchange));
                } else {
                    logger.warn("handle {}", path);
                }
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn("error {} {}", path, Exceptions.getMessage(e));
            e.printStackTrace(System.err);            
        }
    }

    private void handle(ChronicHttpxHandler handler, Httpx httpx) {
        try {
            httpx.sendResponse(handler.handle(app, httpx));
        } catch (Exception e) {
            httpx.sendError(e);
        }
        httpx.close();
    }

}

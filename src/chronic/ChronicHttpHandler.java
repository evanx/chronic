/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic;

import chronic.post.PostHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}

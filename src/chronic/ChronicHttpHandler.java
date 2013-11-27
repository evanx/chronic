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

/**
 *
 * @author evan.summers
 */
public class ChronicHttpHandler implements HttpHandler {
    final static int contentLengthLimit = 4000;
    
    Logger logger = LoggerFactory.getLogger(ChronicHttpHandler.class);
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
                new PostHttpHandler(app).handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}

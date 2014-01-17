/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class Resolve implements HttpHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Resolve.class);
 
    private final Map<String, String> map;

    public Resolve(Map<String, String> map) {
        this.map = map;
    }
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        Httpx httpx = new Httpx(he);
        String orgDomain = httpx.readString();
        String server = map.get(orgDomain);
        if (server == null) {
            logger.warn("not found: {}", orgDomain);
            httpx.sendPlainResponse("secure.chronica.co");
        } else {
            httpx.sendPlainResponse(server);
        }
        httpx.close();                
    }
}

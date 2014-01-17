/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class Forward implements HttpHandler {
    
    static Logger logger = LoggerFactory.getLogger(Forward.class);
    final int port;
    
    public Forward(int port) {
        this.port = port;
    }
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        String text = he.getRequestHeaders().getFirst("Cookie");
        logger.info("cookie: {}", text);
        
    }
    
    public static void copyStream(final int capacity, InputStream input, 
            OutputStream output) throws IOException {
        byte[] buffer = new byte[capacity];
        while (true) {
            int length = input.read(buffer);
            if (length == -1) {
                return;
            }
            output.write(buffer, 0, length);
        }
    }
}

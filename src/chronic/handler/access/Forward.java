/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class Forward implements HttpHandler {
    
    static Logger logger = LoggerFactory.getLogger(Forward.class);
    
    public Forward() {
    }
    
    @Override
    public void handle(HttpExchange http) throws IOException {
        String text = http.getRequestHeaders().getFirst("Cookie");
        logger.info("cookie: {}", text);
        String url = "https://secure.chronica.co:8443" + http.getRequestURI().getPath();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        copyStream(256, http.getRequestBody(), connection.getOutputStream());
        copyStream(1024, connection.getInputStream(), http.getResponseBody());
        http.close();
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

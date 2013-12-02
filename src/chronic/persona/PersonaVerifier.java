/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.persona;

import chronic.util.JsonObjectWrapper;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMapException;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class PersonaVerifier {

    static Logger logger = LoggerFactory.getLogger(PersonaVerifier.class);
    
    public static PersonaUserInfo getUserInfo(String serverUrl, String assertion) 
            throws IOException, JMapException, PersonaException {
        logger.trace("getUserInfo {}", serverUrl);
        URL url = new URL("https://verifier.login.persona.org/verify");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        StringBuilder builder = new StringBuilder();
        builder.append("assertion=").append(assertion);
        builder.append("&audience=").append(URLEncoder.encode(serverUrl, "UTF-8"));
        logger.trace("request", url, builder.toString());
        connection.getOutputStream().write(builder.toString().getBytes());
        JsonObjectWrapper object = new JsonObjectWrapper(connection.getInputStream());
        if (object.hasProperty("status")) {
            String status = object.getString("status");
            if (status.equals("okay")) {
                return new PersonaUserInfo(object.getMap());
            } else {
                String reason = object.getString("reason");
                logger.warn("{}: {}", status, reason);
                throw new PersonaException(status, reason);
            }
        } else {
            logger.warn("status {}", object.keySet());
            throw new PersonaException("status not found");
        }
    }
}

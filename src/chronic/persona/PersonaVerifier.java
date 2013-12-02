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
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class PersonaVerifier {

    Logger logger = LoggerFactory.getLogger(getClass());
    String serverUrl;

    public PersonaVerifier(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public PersonaUserInfo getUserInfo(String assertion) 
            throws IOException, JMapException, PersonaException {
        logger.trace("getUserInfo {}", assertion.length());
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
                throw new PersonaException(object.toString());
            }
        } else {
            logger.warn("status {}", object.keySet());
            throw new PersonaException("status not found");
        }
    }

    @Override
    public String toString() {
        return Args.format(serverUrl);
    }
}

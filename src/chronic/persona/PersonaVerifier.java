/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.persona;

import chronic.util.JsonObjectWrapper;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class PersonaVerifier {

    Logr logger = LogrFactory.getLogger(getClass());
    String serverUrl;

    public PersonaVerifier(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public PersonaUserInfo getUserInfo(String assertion) throws Exception {
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
                return new PersonaUserInfo(object.getProperties());
            } else {
                logger.warn("status {}", status);
            }
        } else {
            logger.warn("status {}", object.keySet());
        }
        return null;
    }

    @Override
    public String toString() {
        return Args.format(serverUrl);
    }
}

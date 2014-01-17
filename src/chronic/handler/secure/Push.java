/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.secure;

import chronic.app.ChronicHttpx;
import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class Push implements PlainHttpxHandler {

    Logger logger = LoggerFactory.getLogger(Push.class);

    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        String pushUrl = httpx.readString();
        logger.info("pushUrl {}", pushUrl);
        if (!cert.isEnabled()) {
            return "Cert not enabled\n";
        }
        return "OK\n";
    }
}

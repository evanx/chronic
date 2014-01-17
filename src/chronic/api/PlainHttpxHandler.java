/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.app.ChronicHttpx;

/**
 *
 * @author evan.summers
 */
public interface PlainHttpxHandler {
    
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) throws Exception;
}

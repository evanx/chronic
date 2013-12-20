/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import chronic.app.ChronicApp;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface ChronicHttpxHandler {
    
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception;
}

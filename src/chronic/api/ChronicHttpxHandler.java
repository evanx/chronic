/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface ChronicHttpxHandler {
    
    public JMap handle(ChronicApp app, ChronicHttpx httpx) throws Exception;
}

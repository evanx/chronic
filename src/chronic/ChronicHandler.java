/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic;

import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface ChronicHandler {
    
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception;
}

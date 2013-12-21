/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface ChronicHttpxHandler {
    
    public JMap handle(ChronicHttpx httpx) throws Exception;
}

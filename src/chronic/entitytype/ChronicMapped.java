/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.app.ChronicApp;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface ChronicMapped {
    public JMap getMap(ChronicApp app);
}

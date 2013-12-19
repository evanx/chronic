/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.app.ChronicApp;

/**
 *
 * @author evan.summers
 */
public interface ChronicApped {
    public void inject(ChronicApp app) throws Exception;
}

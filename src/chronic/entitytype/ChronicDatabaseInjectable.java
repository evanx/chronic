/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.entitytype;

import chronic.app.ChronicDatabase;

/**
 *
 * @author evan.summers
 */
public interface ChronicDatabaseInjectable {
    public void inject(ChronicDatabase db) throws Exception;
}

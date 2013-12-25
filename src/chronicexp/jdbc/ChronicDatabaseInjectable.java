/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronicexp.jdbc;

import chronicexp.entitymap.ChronicDatabase;

/**
 *
 * @author evan.summers
 */
public interface ChronicDatabaseInjectable {
    public void inject(ChronicDatabase db) throws Exception;
}

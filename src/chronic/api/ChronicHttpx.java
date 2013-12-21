/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import chronic.app.ChronicApp;
import chronic.app.ChronicDatabase;
import chronic.entitytype.ChronicDatabaseInjectable;
import com.sun.net.httpserver.HttpExchange;
import java.util.Collection;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpx extends Httpx {

    public ChronicApp app;
    public ChronicDatabase db;

    public ChronicHttpx(ChronicApp app, HttpExchange delegate) {
        super(delegate);
        this.app = app;
    }

    public void setDatabase(ChronicDatabase database) {
        this.db = database;
    }
    
    public void injectDatabase(Collection<? extends ChronicDatabaseInjectable> collection) 
            throws Exception {
        for (ChronicDatabaseInjectable element : collection) {
            element.inject(db);
        }
    }

    
}

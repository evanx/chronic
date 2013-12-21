/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.api;

import chronic.app.ChronicApp;
import chronic.app.ChronicDatabase;
import chronic.entitytype.ChronicDatabaseInjectable;
import com.sun.net.httpserver.HttpExchange;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpx extends Httpx {
    Logger logger = LoggerFactory.getLogger(ChronicHttpx.class);

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
        logger.info("injectDatabase collection {}", collection);
        for (ChronicDatabaseInjectable element : collection) {
            logger.info("injectDatabase element {} {}", element.getClass(), element);
            element.inject(db);
        }
    }

    
}

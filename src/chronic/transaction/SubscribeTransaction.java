/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.AdminUser;
import chronic.entity.Org;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class SubscribeTransaction {
    
    static Logger logger = LoggerFactory.getLogger(SubscribeTransaction.class);
    ChronicApp app;
    Org org;
    AdminUser user;
    
    public SubscribeTransaction(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(String orgUrl, String email) throws StorageException {
        logger.info("handle {} {}", orgUrl, email);
    }
}

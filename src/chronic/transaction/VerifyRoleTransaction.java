/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class VerifyRoleTransaction {
    
    static Logger logger = LoggerFactory.getLogger(VerifyRoleTransaction.class);

    public void handle(ChronicApp app, String orgUrl, String email) throws StorageException {
        new EnrollTransaction().handle(app, orgUrl, email);
    }
}

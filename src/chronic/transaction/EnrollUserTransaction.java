/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollUserTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollUserTransaction.class);
    
    public User handle(ChronicApp app, String email) throws StorageException {
        logger.info("handle {}", email);
        User user = app.storage().user().select(email);
        if (user == null) {
            user = new User(email);
            app.storage().user().insert(user);
        }
        return user;
    }
}
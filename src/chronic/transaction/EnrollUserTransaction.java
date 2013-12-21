/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.api.ChronicHttpx;
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
    
    public User handle(ChronicHttpx httpx, String email) throws StorageException {
        logger.info("handle {}", email);
        User user = httpx.db.user().find(email);
        if (user == null) {
            user = new User(email);
            httpx.db.user().add(user);
        }
        return user;
    }
}

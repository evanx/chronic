/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollCertSubscriberTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollCertSubscriberTransaction.class);
    
    public void handle(ChronicApp app, Cert cert, String email) throws StorageException {
        logger.info("handle {} {}", cert, email);
        User user = app.storage().user().find(email);
        if (user == null) {
            user = new User(email);
            app.storage().user().add(user);
        }
        for (Topic topic : app.storage().topic().list(cert.getKey())) {
            new EnrollSubscriberTransaction().handle(app, topic, email);
        }
        
    }
}

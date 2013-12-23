/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.mail;

import vellum.mail.Mailer;
import vellum.mail.MailerProperties;
import java.io.IOException;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class MailerTest {
    static Logger logger = LoggerFactory.getLogger(MailerTest.class);
    
    public static void main(String[] args) {
        try {
            byte[] bytes = Streams.readBytes(MailerTest.class.getResourceAsStream("app.png"));
            MailerProperties mailerProperties = new MailerProperties();
            mailerProperties.init(bytes, "appcentral.info", "alerts@appcentral.info");
            Mailer mailer = new Mailer(mailerProperties);
            mailer.send("evan.summers@gmail.com", "test subject", 
                    "test body <hr> <img src='cid:image'/>");
        } catch (IOException | MessagingException e) {
            e.printStackTrace(System.err);
        }
    }
}
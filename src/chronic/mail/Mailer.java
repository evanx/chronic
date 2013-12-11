/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.mail;

import chronic.ChronicMessenger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class Mailer {
    static Logger logger = LoggerFactory.getLogger(Mailer.class);

    MailerProperties properties;
    Session session;

    public Mailer(MailerProperties properties) {
        this.properties = properties;
    }

    public void sendEmail(String recipient, String subject, String htmlContent) {
        logger.info("sendEmail {} [{}]", recipient, subject);
        if (properties != null && properties.isEnabled()) {
            try {
                send(recipient, subject, htmlContent);
            } catch (MessagingException | IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    private synchronized void send(String recipient, String subject, String htmlContent) 
            throws MessagingException, IOException {
        logger.info("send {} [{}]", recipient, subject);
        Properties props = new Properties();
        props.put("mail.smtp.host", properties.getHost());
        props.put("mail.smtp.port", properties.getPort());
        if (properties.getUsername() != null) {
            props.put("mail.smtp.auth", true);
            Authenticator auth = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(properties.getUsername(), 
                            properties.getPassword());
                }
            };
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getInstance(props);
        }
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(properties.getFrom()));
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
        message.setHeader("Organization", properties.getOrganisation());
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html");
        MimeMultipart multipart = new MimeMultipart("related");
        multipart.addBodyPart(htmlPart);
        if (properties.getLogoBytes() != null) {
            BodyPart dataPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(
                    new ByteArrayInputStream(properties.getLogoBytes()), "image/png");
            dataPart.setDataHandler(new DataHandler(source));
            dataPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(dataPart);
        }
        message.setContent(multipart);
        Transport.send(message);
    }
    
    public static void main(String[] args) {
        try {
            byte[] bytes = Streams.readBytes(Mailer.class.getResourceAsStream("app.png"));
            MailerProperties mailerProperties = new MailerProperties(bytes, 
                    "appcentral.info", "alerts@appcentral.info");
            Mailer mailer = new Mailer(mailerProperties);
            mailer.sendEmail("evan.summers@gmail.com", "test subject", 
                    "test body <hr> <img src='cid:image'/>");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
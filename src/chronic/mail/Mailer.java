/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.mail;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class Mailer {

    byte[] logoBytes;
    String organisation;
    String from;
    String username;
    String password;
    String host = "localhost";
    int port = 25;
    Session session;

    public Mailer(byte[] logoBytes, String organisation, String from) {
        this.logoBytes = logoBytes;
        this.organisation = organisation;
        this.from = from;
    }

    public void sendEmail(String recipient, String subject, String htmlContent) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        if (username != null) {
            props.put("mail.smtp.auth", true);
            Authenticator auth = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getInstance(props);
        }
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
        message.setHeader("Organization", organisation);
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html");
        MimeMultipart multipart = new MimeMultipart("related");
        multipart.addBodyPart(htmlPart);
        if (logoBytes != null) {
            BodyPart dataPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(
                    new ByteArrayInputStream(logoBytes), "image/png");
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
            Mailer mailer = new Mailer(bytes, "appcentral.info", "alerts@appcentral.info");
            mailer.sendEmail("evan.summers@gmail.com", "test subject", 
                    "test body <hr> <img src='cid:image'/>");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
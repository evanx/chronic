/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.mail;

import vellum.datatype.Emails;
import vellum.util.Args;
import vellum.util.ExtendedProperties;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class MailerProperties {

    byte[] logoBytes;
    String organisation;
    String from;
    String username;
    String password;
    String host = "localhost";
    int port = 25;
    boolean enabled = true;

    public MailerProperties(ExtendedProperties properties) {
        String logoImagePath = properties.getString("logo", null);
        if (logoImagePath != null) {
            logoBytes = Streams.readBytes(logoImagePath);
        }
        from = properties.getString("from");
        organisation = properties.getString("organisation", null);
        if (organisation == null) {
            organisation = Emails.getDomain(from);
        }
        username = properties.getString("username", null);
        password = properties.getString("password", null);
    }
    
    public MailerProperties(byte[] logoBytes, String organisation, String from) {
        this.logoBytes = logoBytes;
        this.organisation = organisation;
        this.from = from;
        this.enabled = true;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
    public byte[] getLogoBytes() {
        return logoBytes;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getFrom() {
        return from;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }       

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return Args.format(organisation, from, logoBytes.length);
    }
    
    
}
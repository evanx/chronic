/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.mail;

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
    
    public MailerProperties(byte[] logoBytes, String organisation, String from) {
        this.logoBytes = logoBytes;
        this.organisation = organisation;
        this.from = from;
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
       
}
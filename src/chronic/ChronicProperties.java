/*
 * Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic;

import chronic.mail.Mailer;
import chronic.mail.MailerProperties;
import chronic.util.JsonObjectWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import vellum.datatype.Millis;
import vellum.httpserver.HttpServerProperties;
import vellum.util.ExtendedProperties;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class ChronicProperties {

    private String alertScript = "scripts/alert.sh";
    private long period = Millis.fromMinutes(3);
    private boolean testing = false;
    private HttpServerProperties httpRedirectServer = new HttpServerProperties(8080);
    private ExtendedProperties appServer;
    private ExtendedProperties webServer;
    private Set<String> adminDomains;
    private Set<String> adminEmails;
    private Set<String> allowedAddresses;
    private ExtendedProperties properties = new ExtendedProperties(System.getProperties());
    private MailerProperties mailer;

    public void init() throws IOException {
        String confFileName = properties.getString("chronic.json", "chronic.json");
        JsonObjectWrapper object = new JsonObjectWrapper(new File(confFileName));
        alertScript = object.getString("alertScript", alertScript);
        period = object.getMillis("period", period);
        testing = object.getBoolean("testing", testing);
        adminDomains = object.getStringSet("adminDomains");
        adminEmails = object.getStringSet("adminEmails");
        allowedAddresses = object.getStringSet("allowedAddresses");
        allowedAddresses.add("127.0.0.1");
        if (object.hasProperties("httpRedirectServer")) {
            httpRedirectServer = new HttpServerProperties(
                object.getProperties("httpRedirectServer"));
        }
        appServer = object.getProperties("appServer");
        webServer = object.getProperties("webServer");
        byte[] bytes = Streams.readBytes(Mailer.class.getResourceAsStream("app.png"));
        mailer = new MailerProperties(bytes, "appcentral.info", "alerts@appcentral.info");        
    }
    
    public String getAlertScript() {
        return alertScript;
    }

    public long getPeriod() {
        return period;
    }

    public boolean isTesting() {
        return testing;
    }

    public ExtendedProperties getAppServer() {
        return appServer;
    }

    public ExtendedProperties getWebServer() {
        return webServer;
    }

    public HttpServerProperties getHttpRedirectServer() {
        return httpRedirectServer;
    }

    public Set<String> getAdminDomains() {
        return adminDomains;
    }
    
    public Collection<String> getAdminEmails() {
        return adminEmails;
    }

    public Set<String> getAllowedAddresses() {
        return allowedAddresses;
    }   
    
    public MailerProperties getMailer() {
        return mailer;
    }
}

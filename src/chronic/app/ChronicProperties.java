/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic.app;

import chronic.mail.MailerTest;
import vellum.mail.MailerProperties;
import vellum.json.JsonObjectDelegate;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.exception.ParseException;
import vellum.httpserver.HttpServerProperties;
import vellum.jx.JConsoleMap;
import vellum.jx.JMap;
import vellum.jx.JMapsException;
import vellum.system.NullConsole;
import vellum.util.Args;
import vellum.util.MockableConsole;
import vellum.util.Streams;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class ChronicProperties extends JMap {

    static Logger logger = LoggerFactory.getLogger(ChronicProperties.class);

    private final String siteUrl;
    private final String allocateServer;
    private final String mimicEmail;
    private String alertScript = null;
    private long alertPeriod = Millis.fromMinutes(5);
    private long statusPeriod = Millis.fromHours(32);
    private long digestPeriod = Millis.fromMinutes(1);
    private long period = Millis.fromMinutes(2);
    private boolean testing = false;
    private boolean mockStorage = false;
    private HttpServerProperties httpRedirectServer = new HttpServerProperties(8080);
    private final MockableConsole console = new NullConsole();
    private final HttpServerProperties insecureServer = new HttpServerProperties(8081);
    private final JConsoleMap appServer;
    private final JConsoleMap webServer;
    private final JConsoleMap signing;
    private final Set<String> adminDomains;
    private final Set<String> adminEmails;
    private final Set<String> allowedOrgDomains;
    private final Set<String> allowedAddresses;
    private final Set<String> subscriberEmails;
    private final MailerProperties mailerProperties = new MailerProperties();

    public ChronicProperties(JMap properties) throws IOException, ParseException, JMapsException {
        putAll(properties);
        String jsonConfigFileName = properties.getString("config.json", "config.json");
        JsonObjectDelegate object = new JsonObjectDelegate(new File(jsonConfigFileName));
        siteUrl = object.getString("siteUrl");
        allocateServer = object.getString("allocateServer");
        alertScript = object.getString("alertScript", alertScript);
        mimicEmail = object.getString("mimicEmail", null);
        alertPeriod = object.getMillis("alertPeriod", alertPeriod);
        statusPeriod = object.getMillis("statusPeriod", statusPeriod);
        digestPeriod = object.getMillis("digestPeriod", digestPeriod);
        period = object.getMillis("period", period);
        testing = object.getBoolean("testing", testing);
        mockStorage = object.getBoolean("mockStorage", false);
        adminDomains = object.getStringSet("adminDomains");
        adminEmails = object.getStringSet("adminEmails");
        subscriberEmails = object.getStringSet("subscriberEmails");
        subscriberEmails.addAll(adminEmails);
        allowedOrgDomains = object.getStringSet("allowedOrgDomains");
        allowedAddresses = object.getStringSet("allowedAddresses");
        allowedAddresses.add("127.0.0.1");
        if (object.hasProperties("httpRedirectServer")) {
            httpRedirectServer = new HttpServerProperties(
                    object.getProperties("httpRedirectServer"));
        }
        appServer = new JConsoleMap(console, object.getMap("appServer"));
        webServer = new JConsoleMap(console, object.getMap("webServer"));
        signing = new JConsoleMap(console, object.getMap("signing"));
        mailerProperties.init(object.getMap("mailer"));
        mailerProperties.setLogoBytes(Streams.readBytes(MailerTest.class.getResourceAsStream("app48.png")));
        logger.info("mailer {}", mailerProperties);
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getAllocateServer() {
        return allocateServer;
    }
    
    public String getAlertScript() {
        return alertScript;
    }

    public long getPeriod() {
        return period;
    }

    public long getDigestPeriod() {
        return digestPeriod;
    }
    
    public long getAlertPeriod() {
        return alertPeriod;
    }

    public long getStatusPeriod() {
        return statusPeriod;
    }
        
    public boolean isTesting() {
        return testing;
    }

    public boolean isTesting(String name) {
        return false;
    }

    public JConsoleMap getAppServer() {
        return appServer;
    }

    public JConsoleMap getWebServer() {
        return webServer;
    }

    public JConsoleMap getSigning() {
        return signing;
    }
    
    public HttpServerProperties getHttpRedirectServer() {
        return httpRedirectServer;
    }

    public HttpServerProperties getInsecureServer() {
        return insecureServer;
    }    
        
    public Set<String> getAdminDomains() {
        return adminDomains;
    }

    public Collection<String> getAdminEmails() {
        return adminEmails;
    }

    public String getAdminEmail() {
        return adminEmails.iterator().next();
    }
    
    public Set<String> getAllowedOrgDomains() {
        return allowedOrgDomains;
    }

    public Set<String> getAllowedAddresses() {
        return allowedAddresses;
    }

    public MailerProperties getMailerProperties() {
        return mailerProperties;
    }

    public boolean isAdmin(String email) {
        return adminEmails.contains(email) || Strings.endsWith(email, adminDomains);
    }

    public boolean isSubscriber(String email) {
        return subscriberEmails.contains(email) || isAdmin(email);
    }

    public PoolProperties getPoolProperties() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl("jdbc:postgresql://localhost:5432/chronica");
        poolProperties.setDriverClassName("org.postgresql.Driver");
        poolProperties.setUsername("chronica");
        poolProperties.setPassword("chronica");
        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxActive(100);
        poolProperties.setInitialSize(10);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMinIdle(10);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return poolProperties;
    }

    public boolean isMockStorage() {
        return mockStorage;
    }

    public String getMimicEmail() {
        return mimicEmail;
    }
        
    @Override
    public String toString() {
        return Args.format(siteUrl);
    }    

    public boolean isAllowedDomain(String orgDomain) {
        return true;
    }

    public boolean isAllowedAddress(String remoteHostAddress) {
        return true;
    }      
}

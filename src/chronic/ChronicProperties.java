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

import chronic.util.JsonObjectWrapper;
import java.io.IOException;
import vellum.datatype.Millis;
import vellum.httpserver.HttpServerProperties;
import vellum.util.ExtendedProperties;

/**
 *
 * @author evan.summers
 */
public class ChronicProperties {

    private String alertScript = "scripts/alert.sh";
    private long period = Millis.fromMinutes(3);
    private boolean testing = false;
    private String serverUrl = "https://localhost:8443";
    private HttpServerProperties httpRedirectServer = new HttpServerProperties(8080);
    private ExtendedProperties appServer;
    private ExtendedProperties webServer;
    private String adminEmail;
    private String remoteAddress = "127.0.0.1";
    private ExtendedProperties properties = new ExtendedProperties(System.getProperties());

    public void init() throws IOException {
        String confFileName = properties.getString("chronic.json", "chronic.json");
        JsonObjectWrapper object = new JsonObjectWrapper(confFileName);
        alertScript = object.getString("alertScript", alertScript);
        period = object.getMillis("period", period);
        testing = object.getBoolean("testing", testing);
        serverUrl = object.getString("serverUrl", serverUrl);
        adminEmail = object.getString("adminEmail", "admin@chronic.net");
        remoteAddress = object.getString("remoteAddress", remoteAddress);
        if (object.hasProperties("httpRedirectServer")) {
            httpRedirectServer = new HttpServerProperties(
                object.getProperties("httpServer"));
        }
        appServer = object.getProperties("appServer");
        webServer = object.getProperties("webServer");
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

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}

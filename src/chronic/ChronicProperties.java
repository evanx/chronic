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

import vellum.datatype.Millis;
import vellum.httpserver.HttpServerProperties;
import vellum.jsonconfig.JsonConfig;
import vellum.util.ExtendedProperties;

/**
 *
 * @author evan.summers
 */
public class ChronicProperties {
    String alertScript = "scripts/alert.sh";
    long period = Millis.fromMinutes(3);
    boolean testing = false;
    String serverUrl = "https://localhost:8443";
    HttpServerProperties httpServer = new HttpServerProperties(8080);
    ExtendedProperties httpsServer;
    String adminEmail;
    String remoteAddress = "127.0.0.1";
    
    public void init(JsonConfig config) {
        alertScript = config.getProperties().getString("alertScript", alertScript);
        period = config.getProperties().getMillis("period", period);
        testing = config.getProperties().getBoolean("testing", testing);
        serverUrl = config.getProperties().getString("serverUrl", serverUrl);
        httpsServer = config.getProperties("httpsServer");
        adminEmail = config.getProperties().getString("adminEmail", null);
        remoteAddress = config.getProperties().getString("remoteAddress", remoteAddress);
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

    public ExtendedProperties getHttpsServer() {
        return httpsServer;
    }

    public HttpServerProperties getHttpServer() {
        return httpServer;
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

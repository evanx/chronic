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
package minima;

import vellum.json.JsonObjectDelegate;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.VellumHttpsServer;
import vellum.httphandler.WebHttpHandler;
import vellum.ssl.OpenTrustManager;
import vellum.util.ExtendedProperties;

/**
 *
 * @author evan.summers
 */
public class MinimaApp {

    Logger logger = LoggerFactory.getLogger(MinimaApp.class);
    VellumHttpsServer webServer = new VellumHttpsServer();
    WebHttpHandler webHandler = new WebHttpHandler("/chronic/web");
    
    public MinimaApp() {
    }

    public void start() throws Exception {
        JsonObjectDelegate object = new JsonObjectDelegate(new File("minima.json"));
        ExtendedProperties webServerProps = object.getProperties("webServer");
        webServer.start(webServerProps,
                new OpenTrustManager(),
                webHandler);
    }
    
    public static void main(String[] args) throws Exception {
        try {
            MinimaApp app = new MinimaApp();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
}

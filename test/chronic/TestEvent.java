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
package chronic;

import chronic.app.ChronicApp;
import chronic4j.ChronicPoster;
import java.security.KeyStore;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.security.KeyStores;

/**
 *
 * @author evans
 */
public class TestEvent {

    static Logger logger = LoggerFactory.getLogger(TestEvent.class);

    ChronicApp app = new ChronicApp();
    ChronicPoster poster = new ChronicPoster();
    String keyStoreLocation = System.getProperty("user.home") + "/.chronica/etc/keystore.jks";
    char[] sslPass = "chronica".toCharArray();
    KeyStore keyStore;
    String resolveUrl = "https://localhost:8444/resolve";
    String serverAddress;
    String postUrl; 
    
    @Test
    public void testResolve() throws Exception {
        app.init();
        keyStore = KeyStores.loadKeyStore("JKS", keyStoreLocation, sslPass);
        poster.init(keyStore, sslPass);
        app.ensureInitialized();
        serverAddress = poster.post(resolveUrl);
        Assert.assertEquals("localhost:8444", serverAddress);
        postUrl = String.format("https://%s/post", serverAddress);
        Assert.assertEquals("OK:", poster.post(postUrl, "OK: test"));
    }

}

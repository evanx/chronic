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
package chronicapp;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.crypto.rsa.RsaKeyStores;
import vellum.datatype.Millis;
import vellum.httpserver.VellumHttpsServer;
import vellum.ssl.SSLContexts;
import vellum.system.Exec;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class ChronicApp implements Runnable {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicConfig config = new ChronicConfig();
    ChronicProperties properties = new ChronicProperties();
    ChronicStorage storage = new ChronicStorage();
    VellumHttpsServer httpsServer;
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    
    public void init() throws Exception {
        config.init();
        properties.init(config.getProperties());
        storage.init();
        char[] keyPassword = Long.toString(new SecureRandom().nextLong() & 
                System.currentTimeMillis()).toCharArray();
        KeyStore keyStore = RsaKeyStores.createKeyStore("JKS", "crom", keyPassword, 365);
        SSLContext sslContext = SSLContexts.create(keyStore, keyPassword, 
                new ChronicTrustManager(this));
        httpsServer = new VellumHttpsServer();
        httpsServer.start(config.getProperties("httpsServer"), sslContext, 
                new ChronicHttpHandler(this));
        logger.info("initialized");
    }

    public void start() throws Exception {
        executorService.schedule(this, 3, TimeUnit.MINUTES);
        logger.info("started");
        if (config.systemProperties.getBoolean("crom.test")) {
            test();
        }
    }
    
    public void test() throws Exception {
        String pattern = "From: [a-z]+ \\(Cron Daemon\\)";
        logger.info("matches {}", "From: root (Cron Daemon)".matches(pattern));
        
    }

    public void stop() throws Exception {
        if (httpsServer != null) {
            httpsServer.shutdown();
        }
        executorService.shutdown();
    }

    public ChronicStorage getStorage() {
        return storage;
    }
    
    protected synchronized void putRecord(StatusRecord statusRecord) {
        StatusRecord previousRecord = recordMap.put(statusRecord.getKey(), statusRecord);
        if (previousRecord == null) {
            alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
        } else {
            AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
            logger.info("putRecord {}", Arrays.toString(new Object[] {
                    previousAlert.getStatusRecord().getStatusType(), 
                    previousRecord.getStatusType(), statusRecord.getStatusType()}));
            if (statusRecord.isAlertable(previousRecord, previousAlert)) {
                AlertRecord alertRecord = new AlertRecord(statusRecord);
                alert(statusRecord);
                alertMap.put(statusRecord.getKey(), alertRecord);
            }
        }
    }
    
    @Override
    public synchronized void run() {
        for (StatusRecord statusRecord : recordMap.values()) {
            AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
            if (previousAlert != null && previousAlert.getStatusRecord() != statusRecord &&
                    statusRecord.getPeriodMillis() != 0) {
                long period = Millis.elapsed(statusRecord.getTimestamp());
                if (period > statusRecord.getPeriodMillis() && 
                        period - statusRecord.getPeriodMillis() > 
                        Millis.fromMinutes(properties.getPeriodMinutes())) {
                        statusRecord.setStatusType(StatusType.ELAPSED);
                        alert(statusRecord);
                }                                    
            }
        }
    }
    
    private synchronized void alert(StatusRecord statusRecord) {
        logger.info("ALERT {}", statusRecord.toString());
        if (properties.getAlertScript() != null) {
            try {
                new Exec().exec(properties.getAlertScript(), statusRecord.getContent(),
                        "from=" + statusRecord.getFrom(),
                        "source=" + statusRecord.getSource(),
                        "status=" + statusRecord.getStatusType(),
                        "subject=" + statusRecord.getSubject(),
                        "alert=" + statusRecord.getAlertString()
                        );
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        try {
            ChronicApp app = new ChronicApp();
            app.init();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}

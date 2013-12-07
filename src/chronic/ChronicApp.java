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

import chronic.type.StatusType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.httphandler.RedirectPortHttpHandler;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.storage.StorageException;
import vellum.type.ComparableTuple;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class ChronicApp implements Runnable {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicProperties properties = new ChronicProperties();
    ChronicStorage storage = ChronicStorage.create(this);
    ChronicMessenger messenger = new ChronicMessenger(this);
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpsServer appServer = new VellumHttpsServer();
    VellumHttpServer httpServer = new VellumHttpServer();
    Map<ComparableTuple, StatusRecord> recordMap = new ConcurrentHashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new ConcurrentHashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public ChronicApp() {
    }

    public void init() throws Exception {        
        properties.init();
        storage.init();
        messenger.init();
        messenger.alertAdmins("Chronic restarted");
        httpServer.start(properties.getHttpRedirectServer(),
                new RedirectPortHttpHandler(properties.getWebServer().getInt("port")));
        webServer.start(properties.getWebServer(), 
                new ChronicTrustManager(this),
                new ChronicHttpHandler(this));
        appServer.start(properties.getAppServer(), 
                new ChronicTrustManager(this),
                new ChronicHttpHandler(this));
        logger.info("initialized");
    }

    public ChronicProperties getProperties() {
        return properties;
    }

    public void start() throws Exception {
        logger.info("schedule {}", properties.getPeriod());
        executorService.scheduleAtFixedRate(this, properties.getPeriod(),
                properties.getPeriod(), TimeUnit.MILLISECONDS);
        logger.info("started");
        if (properties.isTesting()) {
            test();
        }
    }

    public void test() throws Exception {
    }

    public void stop() throws Exception {
        if (webServer != null) {
            webServer.shutdown();
        }
        if (httpServer != null) {
            httpServer.shutdown();
        }
        executorService.shutdown();
    }

    public ChronicStorage getStorage() {
        return storage;
    }

    public Map<ComparableTuple, AlertRecord> getAlertMap() {
        return alertMap;
    }
    
    @Override
    public synchronized void run() {
        logger.info("run {}", properties.getPeriod());
        for (StatusRecord statusRecord : recordMap.values()) {
            if (statusRecord.getPeriodMillis() != 0) {
                checkElapsed(statusRecord);
            }
        }
    }

    private void checkElapsed(StatusRecord status) {
        long elapsed = Millis.elapsed(status.getTimestamp());
        logger.debug("checkElapsed {}: elapsed {}", status.getSource(), elapsed);
        if (elapsed > status.getPeriodMillis() + properties.getPeriod()) {
            AlertRecord previousAlert = alertMap.get(status.getKey());
            if (previousAlert == null || 
                    previousAlert.getStatus().getStatusType() != StatusType.ELAPSED) {
                status.setStatusType(StatusType.ELAPSED);
                AlertRecord alert = new AlertRecord(status);
                alertMap.put(status.getKey(), alert);
                messenger.alert(alert);
            }
        }
    }

    public synchronized void putRecord(StatusRecord status) throws StorageException {
        logger.info("putRecord {} [{}]", status.getStatusType(),
                status.getSubject());
        if (status.getSubscribers() != null) {
            for (String subscriber : status.getSubscribers()) {
                getStorage().subscribe(subscriber, status.getOrgName());
            }
        }
        StatusRecord previousStatus = recordMap.put(status.getKey(), status);
        if (previousStatus == null) {
            logger.info("putRecord: no previous status");
            if (properties.isTesting()) {
                AlertRecord alert = new AlertRecord(status);
                alertMap.put(status.getKey(), alert);
                messenger.alert(alert);
            }
        } else if (status.isAlertable(previousStatus)) {
            AlertRecord alert = new AlertRecord(status, previousStatus);
            alertMap.put(status.getKey(), alert);
            messenger.alert(alert);
        }
    }

    public boolean isAdmin(String email) {
        return properties.getAdminEmails().contains(email) ||
            Strings.endsWith(email, properties.getAdminDomains());        
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

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.httphandler.RedirectHttpHandler;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.json.JsonConfig;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class ChronicApp implements Runnable {

    Logger logger = LoggerFactory.getLogger(getClass());
    JsonConfig config = new JsonConfig();
    ChronicProperties properties = new ChronicProperties();
    ChronicStorage storage = new TemporaryChronicStorage();
    ChronicMessenger messenger = new ChronicMessenger(this);
    VellumHttpsServer httpsServer = new VellumHttpsServer();
    VellumHttpServer httpServer = new VellumHttpServer();
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, StatusRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public ChronicApp() {
    }

    public void init() throws Exception {
        config.init(getClass(), "chronic");
        properties.init(config);
        storage.init();
        httpServer.start(properties.getHttpServer(),
                new RedirectHttpHandler(properties.getServerUrl()));
        httpsServer.start(properties.getHttpsServer(),
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
        if (httpsServer != null) {
            httpsServer.shutdown();
        }
        if (httpServer != null) {
            httpServer.shutdown();
        }
        executorService.shutdown();
    }

    public ChronicStorage getStorage() {
        return storage;
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

    private void checkElapsed(StatusRecord previousStatus) {
        long elapsed = Millis.elapsed(previousStatus.getTimestamp());
        logger.debug("checkElapsed {}: elapsed {}", previousStatus.getSource(), elapsed);
        if (elapsed > previousStatus.getPeriodMillis() + properties.getPeriod()) {
            StatusRecord previousAlert = alertMap.get(previousStatus.getKey());
            if (previousAlert == null
                    || previousAlert.getStatusType() != StatusType.ELAPSED) {
                StatusRecord elapsedStatus = new StatusRecord(previousStatus);
                elapsedStatus.setStatusType(StatusType.ELAPSED);
                alert(elapsedStatus, previousStatus, previousAlert);
            }
        }
    }

    public synchronized void putRecord(StatusRecord status) {
        logger.info("putRecord {} [{}]", status.getStatusType(),
                status.getSubject());
        StatusRecord previousStatus = recordMap.put(status.getKey(), status);
        StatusRecord previousAlert = alertMap.get(status.getKey());
        if (previousStatus == null) {
            logger.info("putRecord: no previous status");
            if (properties.isTesting()) {
                alert(status, status, null);
            }
        } else if (status.isAlertable(previousStatus, previousAlert)) {
            alert(status, previousStatus, previousAlert);
        } else if (previousAlert == null) {
            logger.info("putRecord: no previous alert");
            if (status.isAlertable()) {
                alertMap.put(status.getKey(), new StatusRecord(status));
            }
        }
    }

    private synchronized void alert(StatusRecord status,
            StatusRecord previousStatus, StatusRecord previousAlert) {
        logger.info("alert {}", status.toString());
        alertMap.put(status.getKey(), status);
        messenger.alert(status, previousStatus, previousAlert);
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

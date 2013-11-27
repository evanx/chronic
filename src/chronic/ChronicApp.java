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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.json.JsonConfig;
import vellum.system.Exec;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class ChronicApp implements Runnable {
    Logger logger = LoggerFactory.getLogger(getClass());
    JsonConfig config = new JsonConfig();
    ChronicProperties properties = new ChronicProperties();
    ChronicStorage storage = new ChronicStorage();
    VellumHttpsServer httpsServer = new VellumHttpsServer();
    VellumHttpServer httpServer = new VellumHttpServer();
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, StatusRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void init() throws Exception {
        config.init(getClass(), "chronic");
        properties.init(config);
        storage.init();
        httpServer.start(config.getProperties("httpServer"), new ChronicHttpHandler(this)); 
        httpsServer.start(config.getProperties("httpsServer"), new ChronicTrustManager(this),
                new ChronicHttpHandler(this));
        logger.info("initialized");
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
    
    public synchronized void putRecord(StatusRecord statusRecord) {
        logger.info("putRecord {} [{}]", statusRecord.getStatusType(), statusRecord.getSubject());
        StatusRecord previousStatus = recordMap.put(statusRecord.getKey(), statusRecord);
        StatusRecord previousAlert = alertMap.get(statusRecord.getKey());
        if (previousStatus == null) {
            logger.info("putRecord: no previous status");
            if (properties.isTesting()) {
                alert(statusRecord, statusRecord, null);
            }
        } else if (statusRecord.isAlertable(previousStatus, previousAlert)) {
            alert(statusRecord, previousStatus, previousAlert);
        } else if (previousAlert == null) {
            logger.info("putRecord: no previous alert");
            if (statusRecord.isAlertable()) {
                alertMap.put(statusRecord.getKey(), new StatusRecord(statusRecord));
            }
        }
    }

    private synchronized void alert(StatusRecord status,
            StatusRecord previousStatus, StatusRecord previousAlert) {
        logger.info("alert {}", status.toString());
        alertMap.put(status.getKey(), status);
        if (properties.getAlertScript() != null) {
            try {
                new Exec().exec(properties.getAlertScript(), new AlertBuilder().build(
                        status, previousStatus, previousAlert).getBytes(),
                        status.getAlertMap());
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

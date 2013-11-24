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

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
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
    VellumHttpsServer httpsServer;
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void init() throws Exception {
        config.init(getClass(), "chronic");
        properties.init(config);
        storage.init();
        httpsServer = new VellumHttpsServer();
        httpsServer.start(config.getProperties("httpsServer"), new ChronicTrustManager(this),
                new ChronicHttpHandler(this));
        logger.info("initialized");
    }

    public void start() throws Exception {
        executorService.schedule(this, properties.getPeriod(), TimeUnit.MILLISECONDS);
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

    protected synchronized void putRecord(StatusRecord statusRecord) {
        logger.info("putRecord {} [{}]", statusRecord.getStatusType(), statusRecord.getSubject());
        StatusRecord previousStatus = recordMap.put(statusRecord.getKey(), statusRecord);
        AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
        if (previousStatus == null) {
            if (properties.isTesting()) {
                alert(statusRecord, statusRecord, null);
            }
        } else if (statusRecord.isAlertable(previousStatus, previousAlert)) {
            alert(statusRecord, previousStatus, previousAlert);
        } else if (previousAlert == null) {
            if (statusRecord.isAlertable()) {
                alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
            }
        }
    }

    @Override
    public synchronized void run() {
        for (StatusRecord statusRecord : recordMap.values()) {
            AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
            if (previousAlert != null && previousAlert.getStatusRecord() != statusRecord
                    && statusRecord.getPeriodMillis() != 0) {
                long elapsed = Millis.elapsed(statusRecord.getTimestamp());
                logger.info("run {} elapsed {}", statusRecord.getSource(), elapsed);
                if (elapsed > statusRecord.getPeriodMillis()) {
                    elapsed = elapsed - statusRecord.getPeriodMillis();
                    logger.info("run {} elapsed {}", properties.getPeriod(), elapsed);
                    if (elapsed > properties.getPeriod()) {
                        statusRecord.setStatusType(StatusType.ELAPSED);
                        alert(statusRecord, null, null);
                    }
                }
            }
        }
    }

    private synchronized void alert(StatusRecord statusRecord,
            StatusRecord previousStatusRecord, AlertRecord previousAlertRecord) {
        logger.info("alert {}", statusRecord.toString());
        alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
        if (properties.getAlertScript() != null) {
            try {
                new Exec().exec(properties.getAlertScript(), new AlertBuilder().build(
                        statusRecord, previousStatusRecord, previousAlertRecord).getBytes(),
                        statusRecord.getAlertMap());
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

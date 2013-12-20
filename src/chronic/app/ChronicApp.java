/*
 * Source https://github.com/evanx by @evanxsummers

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
package chronic.app;

import chronic.entitymap.MockChronicStorage;
import chronic.entitytype.ChronicApped;
import chronic.persona.PersonaException;
import chronic.persona.PersonaUserInfo;
import chronic.persona.PersonaVerifier;
import chronic.type.AlertType;
import chronic.type.StatusType;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.collections.SynchronizedCapacityDeque;
import vellum.data.Millis;
import vellum.httpserver.Httpx;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.jx.JMapException;
import vellum.storage.StorageException;
import vellum.data.ComparableTuple;
import vellum.httphandler.RedirectHttpsHandler;

/**
 *
 * @author evan.summers
 */
public class ChronicApp implements Runnable {

    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicProperties properties = new ChronicProperties();
    ChronicStorage storage;
    ChronicMailMessenger messenger = new ChronicMailMessenger(this);
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpsServer appServer = new VellumHttpsServer();
    VellumHttpServer httpServer = new VellumHttpServer();
    Map<ComparableTuple, StatusRecord> recordMap = new ConcurrentHashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new ConcurrentHashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    SynchronizedCapacityDeque statusDeque = new SynchronizedCapacityDeque(100);
    SynchronizedCapacityDeque alertDeque = new SynchronizedCapacityDeque(100);
    DataSource dataSource = new DataSource();
    
    public ChronicApp() {
    }

    public void init() throws Exception {
        properties.init();
        logger.info("properties {}", properties);
        if (properties.isMockStorage()) {
            storage = new MockChronicStorage(this);
        } else {
            dataSource.setPoolProperties(properties.getPoolProperties());
            storage = new MockChronicStorage(this);
        }
        storage.init();
        messenger.init();
        messenger.alertAdmins("Chronic restarted");
        httpServer.start(properties.getHttpRedirectServer(),
                new RedirectHttpsHandler());
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

    public DataSource getDataSource() {
        return dataSource;
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

    public ChronicStorage storage() {
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
        logger.debug("checkElapsed {} {}", elapsed, status);
        if (elapsed > status.getPeriodMillis() + properties.getPeriod()) {
            AlertRecord previousAlert = alertMap.get(status.getKey());
            if (previousAlert == null
                    || previousAlert.getStatus().getStatusType() != StatusType.ELAPSED) {
                status.setStatusType(StatusType.ELAPSED);
                AlertRecord alert = new AlertRecord(status);
                alertMap.put(status.getKey(), alert);
                messenger.alert(alert);
            }
        }
    }

    public synchronized void putRecord(StatusRecord status) throws StorageException {
        logger.info("putRecord {}", status);
        StatusRecord previousStatus = recordMap.put(status.getKey(), status);
        AlertRecord alert = alertMap.get(status.getKey());
        if (previousStatus == null) {
            logger.info("putRecord: no previous status");
            alert = new AlertRecord(status);
            status.setAlertType(AlertType.INITIAL);
            alertMap.put(status.getKey(), alert);
            if (properties.isTesting()) {
                messenger.alert(alert);
            }
        } else if (new StatusRecordChecker(status).isAlertable(previousStatus, alert)) {
            alert = new AlertRecord(status, previousStatus);
            AlertRecord previousAlert = alertMap.put(status.getKey(), alert);
            if (status.getAlertType() == AlertType.INITIAL) {
            } else {
                long elapsed = alert.getTimestamp() - previousAlert.getTimestamp();
                if (elapsed < properties.getAlertPeriod()) {
                    logger.warn("elapsed {} alert {}", elapsed, alert);
                    alert.ignoreCount = previousAlert.ignoreCount + 1;
                } else {
                    messenger.alert(alert);
                }
            }
        } else {
            long period = status.getTimestamp() - previousStatus.getTimestamp();
            logger.info("putRecord period {}", Millis.formatPeriod(period));
            if (status.getPeriodMillis() == 0) {
                if (period > Millis.fromSeconds(55) && period < Millis.fromSeconds(70)) {
                    status.setPeriodMillis(Millis.fromSeconds(60));
                    logger.info("putRecord set period {}", Millis.formatPeriod(period));
                } else if (period > Millis.fromMinutes(55) && period < Millis.fromMinutes(70)) {
                    status.setPeriodMillis(Millis.fromMinutes(60));
                    logger.info("putRecord set period {}", Millis.formatPeriod(period));
                }
            }
        }
    }

    public String getEmail(Httpx httpx) throws JMapException, IOException, PersonaException {
        if (ChronicCookie.matches(httpx.getCookieMap())) {
            ChronicCookie cookie = new ChronicCookie(httpx.getCookieMap());
            if (cookie.getEmail() != null) {
                if (properties.isTesting()) {
                    if (properties.isMimic(httpx) &&
                            properties.isAdmin(cookie.getEmail())) {
                        return properties.getMimicEmail();
                    } else {
                        return cookie.getEmail();
                    }
                }
                PersonaUserInfo userInfo = new PersonaVerifier(this, cookie).
                        getUserInfo(httpx.getHostUrl(), cookie.getAssertion());
                if (cookie.getEmail().equals(userInfo.getEmail())) {
                    return userInfo.getEmail();
                }
            }
        }
        logger.warn("getEmail cookie {}", httpx.getCookieMap());
        httpx.setCookie(ChronicCookie.emptyMap(), ChronicCookie.MAX_AGE_MILLIS);
        throw new PersonaException("no verified email");
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

    public void inject(Collection<? extends ChronicApped> collection) throws Exception {
        for (ChronicApped element : collection) {
            element.inject(this);
        }
    }

}

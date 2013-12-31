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
package chronic.app;

import chronic.alert.StatusRecord;
import chronic.alert.StatusRecordChecker;
import chronic.alert.ChronicMailMessenger;
import chronic.alert.AlertRecord;
import chronic.alert.MetricSeries;
import chronic.alert.MetricValue;
import chronic.entitykey.TopicMetricKey;
import chronic.type.AlertType;
import chronic.type.StatusType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.collections.SynchronizedCapacityDeque;
import vellum.data.Millis;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.data.ComparableTuple;
import vellum.httphandler.RedirectHttpsHandler;
import vellum.ssl.OpenTrustManager;

/**
 *
 * @author evan.summers
 */
public class ChronicApp {

    Logger logger = LoggerFactory.getLogger(ChronicApp.class);
    ChronicProperties properties = new ChronicProperties();
    ChronicMailMessenger messenger = new ChronicMailMessenger(this);
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpsServer appServer = new VellumHttpsServer();
    VellumHttpServer httpRedirectServer = new VellumHttpServer();
    Map<TopicMetricKey, MetricSeries> seriesMap = new ConcurrentHashMap();
    Map<ComparableTuple, StatusRecord> recordMap = new ConcurrentHashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new ConcurrentHashMap();
    ScheduledExecutorService elapsedExecutorService = Executors.newSingleThreadScheduledExecutor();
    SynchronizedCapacityDeque<AlertRecord> alertDeque = new SynchronizedCapacityDeque(100);
    LinkedBlockingQueue<AlertRecord> alertQueue = new LinkedBlockingQueue(100);
    LinkedBlockingQueue<StatusRecord> statusQueue = new LinkedBlockingQueue(100);
    DataSource dataSource = new DataSource();
    EntityManagerFactory emf;
    boolean initalized = false;
    boolean running = true;
    Thread alertThread = new AlertThread();
    Thread statusThread = new StatusThread();
    Thread initThread = new InitThread();

    public ChronicApp() {
        super();
    }

    public void init() throws Exception {
        properties.init();
        logger.info("properties {}", properties);
        webServer.start(properties.getWebServer(), 
                new OpenTrustManager(),
                new ChronicHttpService(this));
        httpRedirectServer.start(properties.getHttpRedirectServer(), new RedirectHttpsHandler());
        appServer.start(properties.getAppServer(), 
                new ChronicTrustManager(this),
                new ChronicSecureHttpService(this));
        messenger.init();
        initThread.start();
    }

    public void ensureInitialized() throws InterruptedException {
        if (!initalized) {
            initThread.join();
        }
    }

    public void initDeferred() throws Exception {
        dataSource.setPoolProperties(properties.getPoolProperties());
        emf = Persistence.createEntityManagerFactory("chronicPU");;
        initalized = true;
        logger.info("initialized");
        statusThread.start();
        alertThread.start();
        logger.info("schedule {}", properties.getPeriod());
        elapsedExecutorService.scheduleAtFixedRate(new ElapsedRunnable(), properties.getPeriod(),
                properties.getPeriod(), TimeUnit.MILLISECONDS);
        logger.info("started");
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    class InitThread extends Thread {

        @Override
        public void run() {
            try {
                initDeferred();
            } catch (Exception e) {
                logger.warn("init", e);
            }
        }
    }

    public ChronicProperties getProperties() {
        return properties;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void shutdown() throws Exception {
        running = false;
        elapsedExecutorService.shutdown();
        if (webServer != null) {
            webServer.shutdown();
        }
        if (httpRedirectServer != null) {
            httpRedirectServer.shutdown();
        }
        if (statusThread != null) {
            statusThread.interrupt();
            statusThread.join(2000);
        }
        if (alertThread != null) {
            alertThread.interrupt();
            alertThread.join(2000);

        }
    }

    public LinkedBlockingQueue<StatusRecord> getStatusQueue() {
        return statusQueue;
    }
    
    ChronicEntityService newEntityService() {
        return new ChronicEntityService(this);
    }

    public Map<TopicMetricKey, MetricSeries> getSeriesMap() {
        return seriesMap;
    }
        
    class AlertThread extends Thread {

        @Override
        public void run() {
            while (running) {
                ChronicEntityService es = newEntityService();
                try {
                    es.begin();
                    AlertRecord alert = alertQueue.poll(60, TimeUnit.SECONDS);
                    if (alert != null) {
                        messenger.alert(alert, 
                                es.listSubscriptionEmails(alert.getStatus().getTopic()));
                    }
                } catch (InterruptedException e) {
                    logger.warn("run", e);
                } catch (Throwable t) {
                    messenger.alert(t);
                } finally {
                    es.close();
                }
            }
        }
    }

    class StatusThread extends Thread {

        @Override
        public void run() {
            while (running) {
                try {
                    StatusRecord status = statusQueue.poll(60, TimeUnit.SECONDS);
                    if (status == null) {
                    } else {
                        int index = 0;
                        for (MetricValue value : status.getMetricList()) {
                            logger.info("series {}", value);
                            if (value != null && value.getValue() != null) {
                                TopicMetricKey key = new TopicMetricKey(status.getTopic().getId(), index++, value.getLabel());
                                MetricSeries series = seriesMap.get(key);
                                if (series == null) {
                                    series = new MetricSeries(90);
                                    seriesMap.put(key, series);
                                }
                                series.add(System.currentTimeMillis(), value.getValue());
                                logger.info("series {} {}", value.getLabel(), series);
                            }                            
                        }
                        checkStatus(status);
                    }
                } catch (InterruptedException e) {
                    logger.warn("run", e);
                } catch (Throwable t) {
                    messenger.alert(t);
                }
            }
        }
    }

    class ElapsedRunnable implements Runnable {

        @Override
        public void run() {
            try {
                for (StatusRecord statusRecord : recordMap.values()) {
                    if (statusRecord.getPeriodMillis() != 0) {
                        checkElapsed(statusRecord);
                    }
                }
            } catch (Exception e) {
                logger.warn("run", e);
            } catch (Throwable t) {
                messenger.alert(t);
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
                alertQueue.add(alert);
            }
        }
        if (!alertThread.isAlive()) {
            logger.warn("alertThread");
        }
        if (!statusThread.isAlive()) {
            logger.warn("statusThread");
        }
    }

    private void checkStatus(StatusRecord status) {
        logger.info("handleStatus {}", status);
        StatusRecord previousStatus = recordMap.put(status.getKey(), status);
        AlertRecord previousAlert = alertMap.get(status.getKey());
        if (previousStatus == null) {
            logger.info("putRecord: no previous status");
            AlertRecord alert = new AlertRecord(status);
            status.setAlertType(AlertType.INITIAL);
            alertMap.put(status.getKey(), alert);
            if (properties.isTesting("alert:initial")) {
                alertQueue.add(alert);
            }
        } else if (status.getAlertType() == AlertType.ONCE) {
            AlertRecord alert = new AlertRecord(status, previousStatus);
            alertMap.put(status.getKey(), alert);
        } else if (new StatusRecordChecker(status).isAlertable(previousStatus, previousAlert)) {
            AlertRecord alert = new AlertRecord(status, previousStatus);
            if (status.getAlertType() == AlertType.INITIAL) {
                alertMap.put(status.getKey(), alert);
            } else {
                long elapsed = alert.getTimestamp() - previousAlert.getTimestamp();
                if (elapsed < properties.getAlertPeriod()) {
                    logger.warn("elapsed {}: {}", elapsed, previousAlert);
                    previousAlert.setIgnoredAlert(alert);
                } else {
                    alertMap.put(status.getKey(), alert);
                    alertQueue.add(alert);
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

    public Map<ComparableTuple, AlertRecord> getAlertMap() {
        return alertMap;
    }

    public static void main(String[] args) throws Exception {
        try {
            ChronicApp app = new ChronicApp();
            app.init();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}

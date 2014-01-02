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

import chronic.alert.TopicMessage;
import chronic.alert.ChronicMailMessenger;
import chronic.alert.AlertEvent;
import chronic.alert.MetricSeries;
import chronic.alert.MetricValue;
import chronic.entitykey.TopicMetricKey;
import chronic.type.AlertEventType;
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
    Map<ComparableTuple, TopicMessage> recordMap = new ConcurrentHashMap();
    Map<ComparableTuple, AlertEvent> alertMap = new ConcurrentHashMap();
    ScheduledExecutorService elapsedExecutorService = Executors.newSingleThreadScheduledExecutor();
    SynchronizedCapacityDeque<AlertEvent> alertDeque = new SynchronizedCapacityDeque(100);
    LinkedBlockingQueue<AlertEvent> alertQueue = new LinkedBlockingQueue(100);
    LinkedBlockingQueue<TopicMessage> messageQueue = new LinkedBlockingQueue(100);
    DataSource dataSource = new DataSource();
    EntityManagerFactory emf;
    boolean initalized = false;
    boolean running = true;
    Thread initThread = new InitThread();
    Thread messageThread = new MessageThread();
    Thread alertThread = new AlertThread();

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
        messageThread.start();
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
        if (messageThread != null) {
            messageThread.interrupt();
            messageThread.join(2000);
        }
        if (alertThread != null) {
            alertThread.interrupt();
            alertThread.join(2000);

        }
    }

    public LinkedBlockingQueue<TopicMessage> getStatusQueue() {
        return messageQueue;
        
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
                    AlertEvent alert = alertQueue.poll(60, TimeUnit.SECONDS);
                    if (alert != null) {
                        messenger.alert(alert, 
                                es.listSubscriptions(alert.getMessage().getTopic()));
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

    class MessageThread extends Thread {

        @Override
        public void run() {
            while (running) {
                try {
                    TopicMessage message = messageQueue.poll(60, TimeUnit.SECONDS);
                    if (message == null) {
                    } else {
                        int index = 0;
                        for (MetricValue value : message.getMetricList()) {
                            logger.info("series {}", value);
                            if (value != null && value.getValue() != null) {
                                TopicMetricKey key = new TopicMetricKey(message.getTopic().getId(), value.getLabel());
                                key.setOrder(index);
                                MetricSeries series = seriesMap.get(key);
                                if (series == null) {
                                    series = new MetricSeries(180);
                                    seriesMap.put(key, series);
                                }
                                series.add(System.currentTimeMillis(), value.getValue());
                                logger.info("series {} {}", value.getLabel(), series);
                            }                            
                        }
                        checkMessage(message);
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
                for (TopicMessage message : recordMap.values()) {
                    if (message.getPeriodMillis() != 0) {
                        checkElapsed(message);
                    }
                }
            } catch (Exception e) {
                logger.warn("run", e);
            } catch (Throwable t) {
                messenger.alert(t);
            }
        }
    }

    private void checkElapsed(TopicMessage message) {
        long elapsed = Millis.elapsed(message.getTimestamp());
        logger.debug("checkElapsed {} {}", elapsed, message);
        if (elapsed > message.getPeriodMillis() + properties.getPeriod()) {
            AlertEvent previousAlert = alertMap.get(message.getKey());
            if (previousAlert == null
                    || previousAlert.getMessage().getStatusType() != StatusType.ELAPSED) {
                message.setStatusType(StatusType.ELAPSED);
                AlertEvent alert = new AlertEvent(message);
                alertMap.put(message.getKey(), alert);
                alertQueue.add(alert);
            }
        }
        if (!alertThread.isAlive()) {
            logger.warn("alertThread");
        }
        if (!messageThread.isAlive()) {
            logger.warn("statusThread");
        }
    }

    private void checkMessage(TopicMessage message) {
        logger.info("handleStatus {}", message);
        TopicMessage previousMessage = recordMap.put(message.getKey(), message);
        AlertEvent previousAlert = alertMap.get(message.getKey());
        if (previousMessage == null) {
            logger.info("putRecord: no previous status");
            AlertEvent alert = new AlertEvent(message);            
            alert.setAlertEventType(AlertEventType.INITIAL);
            alertMap.put(message.getKey(), alert);
            if (properties.isTesting("alert:initial")) {
                alertQueue.add(alert);
            }
        } else if (message.getStatusType() == StatusType.CONTENT_ERROR) {
            AlertEvent alert = new AlertEvent(message, previousMessage);
            alertMap.put(message.getKey(), alert);
        } else if (message.isAlertable(previousMessage, previousAlert)) {
            AlertEvent alert = new AlertEvent(message, previousMessage);
            if (previousAlert.getAlertEventType() == AlertEventType.INITIAL && 
                       !previousAlert.getMessage().isStatusAlertable()) {
                previousAlert.setAlertEventType(AlertEventType.INITIAL);
                alertMap.put(message.getKey(), alert);
            } else {
                long elapsedMillis = alert.getTimestamp() - previousAlert.getTimestamp();
                if (elapsedMillis < properties.getAlertPeriod()) {
                    logger.warn("alert period not elapsed {}: {}", elapsedMillis, previousAlert);
                    previousAlert.setIgnoredAlert(alert);
                } else {
                    alertMap.put(message.getKey(), alert);
                    alertQueue.add(alert);
                }
            }
        } else {
            long period = message.getTimestamp() - previousMessage.getTimestamp();
            logger.info("putRecord period {}", Millis.formatPeriod(period));
            if (message.getPeriodMillis() == 0) {
                if (period > Millis.fromSeconds(55) && period < Millis.fromSeconds(70)) {
                    message.setPeriodMillis(Millis.fromSeconds(60));
                    logger.info("putRecord set period {}", Millis.formatPeriod(period));
                } else if (period > Millis.fromMinutes(55) && period < Millis.fromMinutes(70)) {
                    message.setPeriodMillis(Millis.fromMinutes(60));
                    logger.info("putRecord set period {}", Millis.formatPeriod(period));
                }
            }
        }
    }

    public Map<ComparableTuple, AlertEvent> getAlertMap() {
        return alertMap;
    }   
}

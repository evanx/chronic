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
import chronic.alert.ChronicAlerter;
import chronic.alert.TopicEvent;
import chronic.alert.MetricSeries;
import chronic.alert.MetricValue;
import chronic.alert.TopicMessageChecker;
import chronic.entity.Alert;
import chronic.entitykey.SubscriptionKey;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicMetricKey;
import chronic.type.AlertEventType;
import chronic.type.AlertType;
import chronic.type.StatusType;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.httphandler.RedirectHttpsHandler;
import vellum.ssl.OpenTrustManager;

/**
 *
 * @author evan.summers
 */
public class ChronicApp {

    Logger logger = LoggerFactory.getLogger(ChronicApp.class);
    ChronicProperties properties = new ChronicProperties();
    ChronicAlerter alerter = new ChronicAlerter(this);
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpsServer appServer = new VellumHttpsServer();
    VellumHttpServer httpRedirectServer = new VellumHttpServer();
    VellumHttpServer insecureServer = new VellumHttpServer();
    Map<TopicMetricKey, MetricSeries> seriesMap = new ConcurrentHashMap();
    LinkedBlockingQueue<TopicMessage> messageQueue = new LinkedBlockingQueue(100);
    Map<TopicKey, TopicMessage> messageMap = new ConcurrentHashMap();
    Map<TopicKey, TopicEvent> eventMap = new ConcurrentHashMap();
    LinkedBlockingQueue<TopicEvent> eventQueue = new LinkedBlockingQueue(100);
    LinkedList<TopicEvent> eventList = new LinkedList();
    Map<SubscriptionKey, Alert> alertMap = new ConcurrentHashMap();
    EntityManagerFactory emf;
    boolean initalized = false;
    boolean running = true;
    Thread initThread = new InitThread();
    Thread messageThread = new MessageThread();
    Thread alertThread = new EventThread();
    ScheduledExecutorService elapsedExecutorService = Executors.newSingleThreadScheduledExecutor();

    public ChronicApp() {
        super();
    }

    public void init() throws Exception {
        properties.init();
        logger.info("properties {}", properties);
        webServer.start(properties.getWebServer(), 
                new OpenTrustManager(),
                new ChronicHttpService(this));
        httpRedirectServer.start(properties.getHttpRedirectServer(), 
                new RedirectHttpsHandler());
        insecureServer.start(properties.getInsecureServer(), 
                new ChronicInsecureHttpService(this));
        appServer.start(properties.getAppServer(), 
                new ChronicTrustManager(this),
                new ChronicSecureHttpService(this));
        alerter.init();
        initThread.start();
    }

    public void ensureInitialized() throws InterruptedException {
        if (initThread.isAlive()) {
            initThread.join();
        }
    }

    public void initDeferred() throws Exception {
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

    public LinkedBlockingQueue<TopicMessage> getMessageQueue() {
        return messageQueue;
        
    }
    
    ChronicEntityService newEntityService() {
        return new ChronicEntityService(this);
    }

    public Map<TopicMetricKey, MetricSeries> getSeriesMap() {
        return seriesMap;
    }
        
    class EventThread extends Thread {

        @Override
        public void run() {
            while (running) {
                ChronicEntityService es = newEntityService();
                try {
                    es.begin();
                    TopicEvent topicEvent = eventQueue.poll(60, TimeUnit.SECONDS);
                    if (topicEvent == null) {
                    } else if (eventMap.get(topicEvent.getMessage().getKey()) != topicEvent) {
                        logger.warn("event from queue differs to latest event in map");
                    } else {
                        alerter.alert(es, topicEvent);
                    }
                } catch (InterruptedException e) {
                    logger.warn("run", e);
                } catch (Throwable t) {
                    alerter.alert(t);
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
                    if (message != null) {
                        handleMessage(message);
                    }
                } catch (InterruptedException e) {
                    logger.warn("run", e);
                } catch (Throwable t) {
                    alerter.alert(t);
                }
            }
        }
    }

    class ElapsedRunnable implements Runnable {

        @Override
        public void run() {
            try {
                for (TopicMessage message : messageMap.values()) {
                    if (message.getPeriodMillis() != 0) {
                        checkElapsed(message);
                    }
                }
            } catch (Exception e) {
                logger.warn("run", e);
            } catch (Throwable t) {
                alerter.alert(t);
            }
        }
    }

    private void checkElapsed(TopicMessage message) {
        long elapsed = Millis.elapsed(message.getTimestamp());
        logger.debug("checkElapsed {} {}", elapsed, message);
        if (elapsed > message.getPeriodMillis() + properties.getPeriod()) {
            TopicEvent previousAlert = eventMap.get(message.getKey());
            if (previousAlert == null
                    || previousAlert.getMessage().getStatusType() != StatusType.ELAPSED) {
                message.setStatusType(StatusType.ELAPSED);
                TopicEvent alert = new TopicEvent(message);
                eventMap.put(message.getKey(), alert);
                eventQueue.add(alert);
            }
        }
        if (!alertThread.isAlive()) {
            logger.warn("alertThread");
        }
        if (!messageThread.isAlive()) {
            logger.warn("statusThread");
        }
    }

    private void handleMessage(TopicMessage message) {
        int index = 0;
        for (MetricValue value : message.getMetricList()) {
            logger.info("series {}", value);
            if (value != null && value.getValue() != null) {
                TopicMetricKey key = new TopicMetricKey(message.getTopic().getId(), value.getLabel());
                key.setOrder(index);
                MetricSeries series = seriesMap.get(key);
                if (series == null) {
                    series = new MetricSeries(75, 50);
                    seriesMap.put(key, series);
                }
                series.add(message.getTimestamp(), value.getValue());
                logger.info("series {} {}", value.getLabel(), series);
            }
        }
        logger.info("{}", message);
        TopicMessage previousMessage = messageMap.put(message.getKey(), message);
        TopicEvent previousEvent = eventMap.get(message.getKey());
        if (previousMessage == null) {
            logger.info("no previous status");
            TopicEvent event = new TopicEvent(message);            
            event.setAlertEventType(AlertEventType.INITIAL);
            eventMap.put(message.getKey(), event);
        } else if (message.getAlertType() == AlertType.NEVER) {
            TopicEvent event = new TopicEvent(message, previousMessage, previousEvent);
            eventMap.put(message.getKey(), event);
        } else if (message.getStatusType() == StatusType.CONTENT_ERROR) {
            TopicEvent event = new TopicEvent(message, previousMessage, previousEvent);
            eventMap.put(message.getKey(), event);
        } else if (TopicMessageChecker.isAlertable(message, previousMessage, previousEvent)) {
            TopicEvent event = new TopicEvent(message, previousMessage, previousEvent);
            if (previousEvent.getAlertEventType() == AlertEventType.INITIAL && 
                       !previousEvent.getMessage().isStatusAlertable()) {
                previousEvent.setAlertEventType(AlertEventType.INITIAL);
                eventMap.put(message.getKey(), event);
            } else {
                eventMap.put(message.getKey(), event);
                eventQueue.add(event);
            }
        } else {
            long period = message.getTimestamp() - previousMessage.getTimestamp();
            logger.info("period {}", Millis.formatPeriod(period));
            if (message.getPeriodMillis() == 0) {
                if (period > Millis.fromSeconds(55) && period < Millis.fromSeconds(70)) {
                    message.setPeriodMillis(Millis.fromSeconds(60));
                    logger.info("set period {}", Millis.formatPeriod(period));
                } else if (period > Millis.fromMinutes(58) && period < Millis.fromMinutes(62)) {
                    message.setPeriodMillis(Millis.fromMinutes(60));
                    logger.info("set period {}", Millis.formatPeriod(period));
                }
            }
        }
    }

    public Map<TopicKey, TopicEvent> getEventMap() {
        return eventMap;
    }   
}

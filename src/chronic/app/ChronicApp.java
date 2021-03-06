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

import chronic.alert.ChronicAlerter;
import chronic.alert.TopicMessage;
import chronic.alert.ChronicMessenger;
import chronic.alert.TopicEvent;
import chronic.alert.MetricSeries;
import chronic.alert.MetricValue;
import chronic.alert.TopicEventChecker;
import chronic.alert.TopicStatus;
import chronic.entity.Alert;
import chronic.entitykey.SubscriptionKey;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicMetricKey;
import chronic.entitykey.TopicStatusKey;
import chronic.type.StatusType;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.httphandler.RedirectHttpsHandler;
import vellum.jx.JConsoleMap;
import vellum.jx.JMap;
import vellum.mail.Mailer;
import vellum.security.KeyStores;
import vellum.ssl.OpenTrustManager;
import vellum.ssl.SSLContexts;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class ChronicApp {

    Logger logger = LoggerFactory.getLogger(ChronicApp.class);
    ChronicProperties properties;
    Mailer mailer;
    ChronicMessenger messenger = new ChronicMessenger(this);
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpsServer appServer = new VellumHttpsServer();
    VellumHttpServer httpRedirectServer = new VellumHttpServer();
    VellumHttpServer insecureServer = new VellumHttpServer();
    Map<TopicMetricKey, MetricSeries> seriesMap = new ConcurrentHashMap();
    LinkedBlockingQueue<TopicMessage> messageQueue = new LinkedBlockingQueue(100);
    Map<TopicKey, TopicMessage> messageMap = new ConcurrentHashMap();
    Map<TopicKey, TopicEvent> eventMap = new ConcurrentHashMap();
    Map<TopicStatusKey, TopicStatus> statusMap = new ConcurrentHashMap();
    LinkedBlockingQueue<TopicEvent> eventQueue = new LinkedBlockingQueue(100);
    Map<SubscriptionKey, Alert> alertMap = new ConcurrentHashMap();
    Map<String, TopicEvent> sentMap = new HashMap();
    EntityManagerFactory emf;
    boolean initalized = false;
    boolean running = true;
    Thread initThread = new InitThread();
    Thread messageThread = new MessageThread(this);
    Thread eventThread = new EventThread(this);
    ScheduledExecutorService elapsedExecutorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService digestExecutorService = Executors.newSingleThreadScheduledExecutor();
    SigningInfo signingInfo;
    SSLContext proxyClientSSLContext;
    TopicEventChecker eventChecker = new TopicEventChecker(this);

    public ChronicApp(ChronicProperties properties) {
        this.properties = properties;
    }

    public void init() throws Exception {
        proxyClientSSLContext = SSLContexts.create(new OpenTrustManager());
        mailer = new Mailer(properties.getMailerProperties());
        initSigning(properties.getSigning());
        logger.info("properties {}", properties);
        webServer.start(properties.getWebServer(),
                new OpenTrustManager(),
                new WebHttpService(this));
        httpRedirectServer.start(properties.getHttpRedirectServer(),
                new RedirectHttpsHandler());
        insecureServer.start(properties.getInsecureServer(),
                new InsecureHttpService(this));
        appServer.start(properties.getAppServer(),
                new ChronicTrustManager(this),
                new SecureHttpService(this));
        initThread.start();
    }

    public void ensureInitialized() throws InterruptedException {
        logger.info("ensureInitialized");
        if (initThread.isAlive()) {
            initThread.join();
        }
        logger.info("ensureInitialized complete");
    }

    public void initDeferred() throws Exception {
        emf = Persistence.createEntityManagerFactory("chronicPU");;
        initalized = true;
        logger.info("initialized");
        messageThread.start();
        eventThread.start();
        logger.info("schedule {}", properties.getPeriod());
        if (properties.getPeriod() > 0) {
            elapsedExecutorService.scheduleAtFixedRate(new ElapsedRunnable(this), properties.getPeriod(),
                    properties.getPeriod(), TimeUnit.MILLISECONDS);
        }
        if (properties.getDigestPeriod() > 0) {
            digestExecutorService.scheduleAtFixedRate(new DigestRunnable(this), properties.getPeriod(),
                    properties.getDigestPeriod(), TimeUnit.MILLISECONDS);
        }
        logger.info("started");
    }

    private void initSigning(JConsoleMap properties) throws Exception {
        String keyStoreLocation = properties.getString("keyStoreLocation");
        logger.info("signing {}", keyStoreLocation);
        char[] pass = properties.getPassword("pass", null);
        KeyStore keyStore = KeyStores.loadKeyStore("JKS", keyStoreLocation, pass);
        PrivateKey privateKey = KeyStores.findPrivateKey(keyStore, pass);
        X509Certificate cert = KeyStores.findPrivateKeyCertificate(keyStore);
        logger.info("signing {}", cert.getSubjectDN());
        int validityDays = properties.getInt("validityDays", 365);
        signingInfo = new SigningInfo(validityDays, privateKey, cert);
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

    public void shutdown() throws Exception {
        logger.info("shutdown");
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
        if (eventThread != null) {
            eventThread.interrupt();
            eventThread.join(2000);
        }
        logger.info("shutdown complete");
    }

    class MessageThread extends Thread {

        ChronicApp app;

        public MessageThread(ChronicApp app) {
            this.app = app;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    TopicMessage message = messageQueue.poll(60, TimeUnit.SECONDS);
                    if (message != null) {
                        handle(message);
                    }
                } catch (InterruptedException e) {
                    logger.warn("run", e);
                } catch (Throwable t) {
                    messenger.alert(t);
                }
            }
        }

        private void handle(TopicMessage message) {
            int index = 0;
            logger.info("series {}", message.getCert());
            for (MetricValue value : message.getMetricList()) {
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
            TopicMessage previousMessage = messageMap.put(message.getTopicKey(), message);
            if (previousMessage == null) {
                logger.info("no previous status");
                return;
            }
            TopicEvent previousEvent = eventMap.get(message.getTopicKey());
            TopicEvent event = eventChecker.check(message, previousMessage, previousEvent);
            if (event != null) {
                logger.info("event: {}", event);
                if (previousEvent != null) {
                    event.setPreviousStatusType(previousEvent.getMessage().getStatusType());
                    event.setPreviousTimestamp(previousEvent.getTimestamp());
                }
                eventMap.put(message.getTopicKey(), event);
                if (message.getAlertType() == null) {
                    logger.warn("alertType null {}", message);
                } else if (!message.getAlertType().isAlertable()) {
                } else if (event.getEventType() != null) {
                } else if (message.getStatusType() == null) {
                    logger.warn("statusType null {}", message);
                } else if (!message.getStatusType().isKnown()) {
                } else {
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
    }

    class EventThread extends Thread {

        ChronicApp app;

        public EventThread(ChronicApp app) {
            this.app = app;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    TopicEvent topicEvent = eventQueue.poll(60, TimeUnit.SECONDS);
                    if (topicEvent != null) {
                        if (eventMap.get(topicEvent.getMessage().getTopicKey()) != topicEvent) {
                            logger.warn("eventMap");
                        } else {
                            persistEvent(topicEvent);
                            if (isAlertable(topicEvent)) {
                                new ChronicAlerter(app, topicEvent).handle();
                            }
                        }
                    }
                } catch (Throwable t) {
                    messenger.alert(t);
                }
            }
        }

        private boolean isAlertable(TopicEvent topicEvent) {
            TopicMessage message = topicEvent.getMessage();
            TopicStatus status = new TopicStatus(message.getTopic().getId(), message.getStatusType());
            TopicStatus previousStatus = statusMap.put(status.getTopicStatusKey(), status);
            if (previousStatus != null) {
                long elapsed = topicEvent.getTimestamp() - previousStatus.getTimestamp();
                if (elapsed < Millis.fromSeconds(message.getTopic().getStatusPeriodSeconds())) {
                    logger.warn("isAlertable {} {}m", message.getTopic(), Millis.toMinutes(elapsed));
                    return false;
                }
            }
            return true;
        }
    }

    class ElapsedRunnable implements Runnable {

        ChronicApp app;

        public ElapsedRunnable(ChronicApp app) {
            this.app = app;
        }

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
                messenger.alert(t);
            }
        }

        private void checkElapsed(TopicMessage message) {
            long elapsed = Millis.elapsed(message.getTimestamp());
            logger.trace("checkElapsed {} {}", elapsed, message);
            if (message.getPeriodMillis() > 0
                    && elapsed > message.getPeriodMillis() + properties.getPeriod()) {
                TopicEvent previousEvent = eventMap.get(message.getTopicKey());
                if (previousEvent == null
                        || previousEvent.getMessage().getStatusType() != StatusType.ELAPSED) {
                    TopicMessage elapsedMessage = new TopicMessage(message);
                    elapsedMessage.setStatusType(StatusType.ELAPSED);
                    TopicEvent topicEvent = new TopicEvent(elapsedMessage, message);
                    eventMap.put(message.getTopicKey(), topicEvent);
                    eventQueue.add(topicEvent);
                    persistEvent(topicEvent);
                }
            }
            if (!eventThread.isAlive()) {
                logger.warn("alertThread");
            }
            if (!messageThread.isAlive()) {
                logger.warn("statusThread");
            }
        }
    }

    class DigestRunnable implements Runnable {

        ChronicApp app;

        public DigestRunnable(ChronicApp app) {
            this.app = app;
        }

        @Override
        public void run() {
            ChronicEntityService es = new ChronicEntityService(app);
            try {
                es.begin();
                es.commit();
            } catch (Exception e) {
                logger.warn("run", e);
            } catch (Throwable t) {
                messenger.alert(t);
            } finally {
                es.close();
            }

        }

        public void handle() {
        }
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public void persistEvent(TopicEvent event) {
        ChronicEntityService es = new ChronicEntityService(this);
        try {
            es.begin();
            es.persistEvent(event);
            es.commit();
        } catch (PersistenceException e) {
            logger.warn("persist {} {}", event, e);
        } finally {
            es.close();
        }
    }

    public ChronicProperties getProperties() {
        return properties;
    }

    public SSLContext getProxyClientSSLContext() {
        return proxyClientSSLContext;
    }

    public SigningInfo getSigningInfo() {
        return signingInfo;
    }

    public Mailer getMailer() {
        return mailer;
    }

    public LinkedBlockingQueue<TopicMessage> getMessageQueue() {
        return messageQueue;
    }

    public Map<TopicMetricKey, MetricSeries> getSeriesMap() {
        return seriesMap;
    }

    public Map<TopicKey, TopicEvent> getEventMap() {
        return eventMap;
    }

    public Map<SubscriptionKey, Alert> getAlertMap() {
        return alertMap;
    }

    public Map<String, TopicEvent> getSentMap() {
        return sentMap;
    }

    public JMap getMetrics() {
        JMap map = new JMap();
        map.put("messageQueue", messageQueue.size());
        map.put("eventQueue", eventQueue.size());
        map.put("seriesMap", seriesMap.size());
        map.put("messageMap", messageMap.size());
        map.put("eventMap", eventMap.size());
        map.put("statusMap", statusMap.size());
        map.put("alertMap", alertMap.size());
        map.put("sentMap", sentMap.size());
        return map;
    }
}

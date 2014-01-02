/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package chronic4j;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.format.CalendarFormats;
import vellum.ssl.OpenTrustManager;
import vellum.ssl.SSLContexts;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class ChronicAppender extends AppenderSkeleton implements Runnable {

    static Logger logger = LoggerFactory.getLogger(ChronicAppender.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ArrayDeque<LoggingEvent> deque = new ArrayDeque();
    private final long period = TimeUnit.SECONDS.toMillis(60);
    private final long initialDelay = period;
    private String postAddress = "https://chronica.co/post";
    private final int maximumPostLength = 2000;
    private boolean initialized;
    private boolean running;
    private long taskTimestamp;
    private String keyStoreLocation = System.getProperty("user.home") + "/.chronica/etc/keystore.jks";
    private char[] sslPass = "chronica".toCharArray();
    SSLContext sslContext;
    ChronicProcessor processor = new DefaultProcessor();
    
    public ChronicAppender() {
    }

    public ChronicAppender(String postAddress) {
        this.postAddress = postAddress;
    }

    public void setKeyStore(String keyStore) {
        this.keyStoreLocation = keyStore;        
    }

    public void setPass(String pass) {
        this.sslPass = pass.toCharArray();
    }

    public void setProcessorClass(String className) throws Exception {
        processor = (ChronicProcessor) Class.forName(className).newInstance();
    }
    
    @Override
    protected void append(LoggingEvent le) {
        if (le.getLoggerName().equals(logger.getName())) {
        }
        if (!initialized) {
            initialized = true;
            initialize();
        }
        if (running && le.getLevel().toInt() >= Priority.DEBUG_INT) {
            if (taskTimestamp > 0 && System.currentTimeMillis() - taskTimestamp > period * 2) {
                running = false;
                synchronized (deque) {
                    deque.clear();
                }
            } else {
                synchronized (deque) {
                    deque.add(le);
                }
                processor.process(le);
            }
        }
    }

    private void initialize() {
        logger.info("initialize {}", keyStoreLocation);
        scheduledExecutorService.scheduleAtFixedRate(this, initialDelay, period, TimeUnit.MILLISECONDS);
        try {
            if (keyStoreLocation == null || sslPass == null) {
                throw new GeneralSecurityException("Missing parameters for SSL connection: keyStore, pass");
            } else {
                sslContext = SSLContexts.create(keyStoreLocation, sslPass, new OpenTrustManager());
                running = true;
            }
        } catch (IOException | GeneralSecurityException e) {
            logger.error("intialized", e);
        }
    }

    @Override
    public void close() {
        running = false;
        scheduledExecutorService.shutdown();
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public synchronized void run() {
        //logger.info("run {} {}", deque.size(), postAddress);
        taskTimestamp = System.currentTimeMillis();
        Deque<LoggingEvent> snapshot;
        synchronized (deque) {
            snapshot = deque.clone();
            deque.clear();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(processor.buildReport());
        builder.append(String.format("INFO: deque size: %d\n", deque.size()));
        builder.append("INFO:-\n");
        builder.append("Latest events:\n");
        while (snapshot.peek() != null) {
            LoggingEvent event = snapshot.poll();
            String string = event.getMessage().toString();
            if (builder.length() + string.length() + 1 >= maximumPostLength) {
                break;
            }
            builder.append(event.getLevel().toString());
            builder.append(": ");
            builder.append(CalendarFormats.timestampFormat.format(TimeZone.getDefault(), event.getTimeStamp()));
            builder.append(" ");
            builder.append(string);
            builder.append("\n");
        }
        post(builder.toString());
    }

    private void post(String string) {
        //logger.info("post:\n{}", string);
        HttpsURLConnection connection;
        try {
            URL url = new URL(postAddress);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setDoOutput(true);
            connection.getOutputStream().write(string.getBytes());
            connection.getOutputStream().close();
            logger.debug("chronica response {}", Streams.readString(connection.getInputStream()));
        } catch (IOException e) {
            logger.warn("post", e);
        }
    }
}

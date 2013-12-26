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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class ChronicAppender extends AppenderSkeleton implements Runnable {

    static Logger logger = LoggerFactory.getLogger(ChronicAppender.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final long period = TimeUnit.SECONDS.toMillis(60);
    private final ArrayDeque<LoggingEvent> deque = new ArrayDeque();    
    private boolean initialized;
    private boolean running;
    private long sampleTimestamp;
    
    @Override
    protected void append(LoggingEvent le) {
        if (!initialized) {
            initialized = true;
            initialize();
        }
        if (running && le != null && le.getLevel().toInt() >= Priority.INFO_INT) {
            if (sampleTimestamp > 0 && System.currentTimeMillis() - sampleTimestamp > period*2) {
                running = false;
                synchronized (deque) {
                    deque.clear();
                }
            } else {
                synchronized (deque) {
                    deque.add(le);                    
                }
            }
        }
    }

    private void initialize() {
        scheduledExecutorService.scheduleAtFixedRate(this, period, period, TimeUnit.MILLISECONDS);
        running = true;
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
        sampleTimestamp = System.currentTimeMillis();
        Deque<LoggingEvent> snapshot;
        synchronized(deque) {
            snapshot = deque.clone();
            deque.clear();
        }
        
    }
}

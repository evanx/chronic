
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
package chronic;

import chronic.alert.TopicMessage;
import chronic.alert.TopicMessageParser;
import chronic.alert.TopicMessagePatterns;
import chronic.app.ChronicApp;
import chronic.app.ChronicProperties;
import chronic.type.StatusType;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.ParseException;
import vellum.jx.JMap;
import vellum.util.Strings;

/**
 *
 * @author evans
 */
public class TestTopicMessageParser {

    static Logger logger = LoggerFactory.getLogger(TestTopicMessageParser.class);

    ChronicApp app; 
    
    public TestTopicMessageParser() throws Exception {
        app = new ChronicApp(new ChronicProperties(new JMap()));
    }

    @Test
    public void testCron() throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        TopicMessage topicMessage = new TopicMessage();
        TopicMessageParser parser = new TopicMessageParser(app, topicMessage);
        builder.append("From: root (Cron Daemon)\n");
        builder.append("Subject: Cron <root@vm> scripts/minutely.sh\n");
        parser.parse(builder.toString());
        Assert.assertEquals("minutely", topicMessage.getServiceLabel());
        Assert.assertEquals("root@vm", topicMessage.getFrom());
    }
    
    @Test
    public void testMetricValue() throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        TopicMessage topicMessage = new TopicMessage();
        TopicMessageParser parser = new TopicMessageParser(app, topicMessage);
        builder.append("Value: Load 3.5\n");
        parser.parse(builder.toString());
        Assert.assertEquals(1, topicMessage.getMetricList().size());
        Assert.assertEquals("Load", topicMessage.getMetricList().get(0).getLabel());
        Assert.assertEquals(new Float(3.5), topicMessage.getMetricList().get(0).getValue());
    }

    @Test
    public void testSingleStatus() throws IOException, ParseException {
        Deque<String> lineList = new ArrayDeque();
        TopicMessage topicMessage = new TopicMessage();
        TopicMessageParser parser = new TopicMessageParser(app, topicMessage);
        lineList.add("Load CRITICAL - 3.5");
        Matcher matcher = TopicMessagePatterns.SERVICE_STATUS.matcher(lineList.getLast());
        Assert.assertTrue(matcher.find());
        parser.parse(Strings.join("\n", lineList));
        Assert.assertEquals(1, topicMessage.getStatusList().size());
        Assert.assertEquals(StatusType.CRITICAL, topicMessage.getStatusList().get(0).getStatusType());
        Assert.assertEquals(0, topicMessage.getMetricList().size());
        Assert.assertEquals("Load", topicMessage.getServiceLabel());
        Assert.assertEquals(StatusType.CRITICAL, topicMessage.getStatusType());
    }

    @Test
    public void testMultiStatus() throws IOException, ParseException {
        Deque<String> lineList = new ArrayDeque();
        TopicMessage topicMessage = new TopicMessage();
        TopicMessageParser parser = new TopicMessageParser(app, topicMessage);
        lineList.add("Load WARNING - 3.5");
        lineList.add("Disk CRITICAL - 95%");
        Matcher matcher = TopicMessagePatterns.SERVICE_STATUS.matcher(lineList.getLast());
        Assert.assertTrue(matcher.find());
        parser.parse(Strings.join("\n", lineList));
        Assert.assertEquals(0, topicMessage.getMetricList().size());
        Assert.assertEquals(2, topicMessage.getStatusList().size());
        Assert.assertEquals(StatusType.WARNING, topicMessage.getStatusList().get(0).getStatusType());
        Assert.assertEquals(StatusType.CRITICAL, topicMessage.getStatusList().get(1).getStatusType());
        Assert.assertEquals("Disk", topicMessage.getServiceLabel());
        Assert.assertEquals(StatusType.CRITICAL, topicMessage.getStatusType());
    }
}

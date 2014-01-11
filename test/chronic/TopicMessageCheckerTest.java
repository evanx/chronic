
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

import chronic.alert.TopicEvent;
import chronic.alert.TopicMessage;
import chronic.alert.TopicMessageChecker;
import chronic.alert.TopicMessageParser;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.ParseException;

/**
 *
 * @author evans
 */
public class TopicMessageCheckerTest {

    static Logger logger = LoggerFactory.getLogger(TopicMessageCheckerTest.class);


    public TopicMessageCheckerTest() {
    }

    @Test
    public void testSingleStatus() throws IOException, ParseException {
        TopicMessage topicMessage1 = newTopicMessage(
                "Load OK - 10",
                "Disk OK - 20");
        Assert.assertEquals("Load", topicMessage1.getServiceLabel());
        TopicMessage topicMessage2 = newTopicMessage(
                "Load CRITICAL - 10",
                "Disk OK - 20");
        Assert.assertEquals("Load", topicMessage2.getServiceLabel());
        TopicMessage topicMessage3 = newTopicMessage(
                "Load WARNING - 10",
                "Disk CRITICAL - 20");
        Assert.assertEquals("Disk", topicMessage3.getServiceLabel());
        TopicEvent alert1 = new TopicEvent(topicMessage1);
        TopicEvent alert2 = new TopicEvent(topicMessage2);
        Assert.assertFalse(TopicMessageChecker.isAlertable(topicMessage1, topicMessage1, alert1));
        Assert.assertTrue(TopicMessageChecker.isAlertable(topicMessage2, topicMessage2, alert1));
        Assert.assertFalse(TopicMessageChecker.isAlertable(topicMessage3, topicMessage2, alert2));
    }
    
    private TopicMessage newTopicMessage(String ... lines) throws IOException, ParseException {
        TopicMessage topicMessage = new TopicMessage();
        TopicMessageParser parser = new TopicMessageParser(topicMessage);
        return parser.parse(lines);
        
    }
}

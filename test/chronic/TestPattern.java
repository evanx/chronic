
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

import chronic.alert.TopicMessagePatterns;
import chronic.handler.access.Forward;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class TestPattern {

    static Logger logger = LoggerFactory.getLogger(TestPattern.class);

    public TestPattern() {
    }

    @Test
    public void emailHeaderPattern() {
        Matcher matcher = TopicMessagePatterns.HEADER.matcher("X-Cron-Env: test");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("X-Cron-Env", matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
    }

    @Test
    public void serverPatternDesign() {
        final Pattern serverPattern = Pattern.compile(" server=(\\w[^ ;]*)");    
        Matcher matcher = serverPattern.matcher(" server=s123.chronica.co ; ");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("s123.chronica.co", matcher.group(1));
    }
    
    @Test
    public void serverPattern() {
        Matcher matcher = Forward.SERVER_PATTERN.matcher(" server=s123.chronica.co");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("s123.chronica.co", matcher.group(1));
    }
    
    @Test
    public void mimicPattern() {
        Assert.assertTrue("https://chronica/mimic".matches(".*\\Wmimic\\W*.*"));
    }
    
    @Test
    public void cronSubject() {
        Matcher matcher = TopicMessagePatterns.CRON_SUBJECT.matcher(
                "Subject: Cron <root@ip-10-11-12-13> ~/scripts/test.sh");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("root", matcher.group(1));
        Assert.assertEquals("ip-10-11-12-13", matcher.group(2));
        Assert.assertEquals("~/scripts/test.sh", matcher.group(3));
    }
}

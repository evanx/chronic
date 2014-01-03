
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
import chronic.alert.TopicMessageMatcher;
import chronic.alert.TopicMessagePatterns;
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
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
public class PatternTest {

    static Logger logger = LoggerFactory.getLogger(PatternTest.class);

    public PatternTest() {
    }

    @Test
    public void emailHeaderPattern() {
        Matcher matcher = TopicMessagePatterns.HEADER.matcher("X-Cron-Env: test");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("X-Cron-Env", matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
    }

    @Test
    public void mimicPattern() {
        Assert.assertTrue("https://chronica/mimic".matches(".*\\Wmimic\\W*.*"));
    }

    @Test
    public void metricValue() {
        String string = "99.23";
        Pattern pattern = Pattern.compile("([-+]?[0-9]+.[0-9]*)");
        Matcher matcher = pattern.matcher(" " + string + " 0.45");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(string, matcher.group(1));
    }

    public void parseValue() {
        String string = "Load=0.9";
        Pattern pattern = Pattern.compile("(\\w+)[\\s,=]+([+-]?[0-9]+.?[0-9]*)");
        Matcher matcher = pattern.matcher(string);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("Load", matcher.group(1));
        Assert.assertEquals("0.9", matcher.group(2));        
    }

    
    @Test
    public void statusMatches() {
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Service OK - 100ms",
                "Service OK - 200ms"
        ));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Service OK - 100ms",
                "Service UNKNOWN - 200ms"
        ));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Service UNKNOWN - 100ms",
                "Service CRITICAL - 200ms"
        ));
        Assert.assertFalse(TopicMessageMatcher.matches(
                "Service OK - 100ms",
                "Service CRITICAL - 100ms"
        ));
        Assert.assertFalse(TopicMessageMatcher.matches(
                "ServiceA OK - 100ms",
                "ServiceB OK - 100ms"
        ));
    }

    @Test
    public void nagiosPatternDash() {
        Matcher matcher = TopicMessagePatterns.SERVICE_STATUS.matcher("Service CRITICAL - no connection");
        Assert.assertTrue(matcher.find());
        Assert.assertTrue(matcher.group(1).equals("Service"));
        Assert.assertTrue(matcher.group(2).equals("CRITICAL"));
        Assert.assertTrue(matcher.group(3).equals("no connection"));
    }

    @Test
    public void nagiosPatternColon() {
        Matcher matcher = TopicMessagePatterns.STATUS.matcher("CRITICAL: no connection");
        Assert.assertTrue(matcher.find());
        Assert.assertTrue(matcher.group(1).equals("CRITICAL"));
        Assert.assertTrue(matcher.group(2).equals("no connection"));
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

    @Test
    public void metricValuePercentPattern() {
        Matcher matcher = Pattern.compile("([0-9]+)(%?)").matcher("50%");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("50", matcher.group(1));
        Assert.assertEquals("%", matcher.group(2));
    }
    
    @Test
    public void metricValuePattern() {
        Matcher matcher = TopicMessagePatterns.HEADER_METRIC_VALUE.matcher(
                "Diskspace 50%");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("Diskspace", matcher.group(1));
        Assert.assertEquals("50", matcher.group(2));
        Assert.assertEquals("%", matcher.group(3));
    }

}


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

import chronic.alert.TopicMessageMatcher;
import chronic.alert.TopicMessagePatterns;
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
public class MetricPatternTest {

    static Logger logger = LoggerFactory.getLogger(MetricPatternTest.class);

    public MetricPatternTest() {
    }

    @Test
    public void metricValuePercentPattern() {
        Matcher matcher = Pattern.compile("([0-9]+)(%?)").matcher("50%");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("50", matcher.group(1));
        Assert.assertEquals("%", matcher.group(2));
    }
    
    @Test
    public void metricValue() {
        String string = "99.23";
        Pattern pattern = TopicMessagePatterns.SERVICE_METRIC_VALUE;
        Matcher matcher = pattern.matcher(" " + string + " 0.45");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(string, matcher.group(1));
    }

    @Test
    public void metricValuePattern() {
        Pattern pattern = TopicMessagePatterns.HEADER_METRIC_VALUE;
        Matcher matcher = pattern.matcher("Diskspace 50%");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("Diskspace", matcher.group(1));
        Assert.assertEquals("50", matcher.group(2));
        Assert.assertEquals("%", matcher.group(3));
    }

    @Test
    public void parseValue() {
        String string = "Load=0.9";
        Pattern pattern = TopicMessagePatterns.HEADER_METRIC_VALUE;
        Matcher matcher = pattern.matcher(string);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("Load", matcher.group(1));
        Assert.assertEquals("0.9", matcher.group(2));        
    }
}

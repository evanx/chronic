
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
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class TopicMessageMatcherTest {

    static Logger logger = LoggerFactory.getLogger(TopicMessageMatcherTest.class);


    public TopicMessageMatcherTest() {
    }

    @Test
    public void statusChanged() {
        String orgDomain = "test.org";
        String orgUnit = "test";
        String commonName = "serverx.test.org";
        CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);        
        Cert cert = new Cert(certKey);
        TopicMessage record1 = new TopicMessage(cert);
        TopicMessage record2 = new TopicMessage(cert);
        record1.getLineList().add("ACME OK - some detail");
        record2.getLineList().add("ACME OK - other detail");
        Assert.assertTrue(TopicMessageMatcher.matches(record1, record2));
        record2 = new TopicMessage(cert);
        record2.getLineList().add("ACMY OK - other detail");
        Assert.assertFalse(TopicMessageMatcher.matches(record1, record2));
        record2 = new TopicMessage(cert);
        record2.getLineList().add("ACME CRITICAL - some detail");
        Assert.assertFalse(TopicMessageMatcher.matches(record1, record2));
    }

    
    @Test
    public void matchesLine() throws IOException {
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Heading: 10", 
                "Heading: 20"));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "OK: 10", 
                "OK: 20"));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "OK: 10", 
                "UNKNOWN: 20"));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "UNKNOWN: 10", 
                "CRITICAL: 20"));
        Assert.assertFalse(TopicMessageMatcher.matches(
                "OK: 10", 
                "WARNING: 10"));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Load OK - 10", 
                "Load OK - 20"));
        Assert.assertTrue(TopicMessageMatcher.matches(
                "Load OK - 10", 
                "Load UNKNOWN - 20"));
        Assert.assertFalse(TopicMessageMatcher.matches(
                "Load OK - 10", 
                "Load WARNING - 10"));
        Assert.assertFalse(TopicMessageMatcher.matches(
                "Load1 OK - 10", 
                "Load2 OK - 10"));
    }
    
    @Test
    public void filterLines() {
        List<String> list = new ArrayList();
        list.add("OK: 10");
        list.add("Load OK - 10");
        list.add("INFO: 10");
        list.add("DEBUG: 10");
        list.add("WARNING: 10");
        list.add("ERROR: 10");
        list.add("Load: 10");
        list.add("Heading: 10");
        int index = list.size();
        list.add("Load - 10");
        Assert.assertEquals(3, TopicMessageMatcher.filterLineList(list).size());
        Assert.assertEquals(list.get(0), TopicMessageMatcher.filterLineList(list).get(0));
        Assert.assertEquals(list.get(1), TopicMessageMatcher.filterLineList(list).get(1));
        Assert.assertEquals(list.get(index), TopicMessageMatcher.filterLineList(list).get(2));
    }
    
}

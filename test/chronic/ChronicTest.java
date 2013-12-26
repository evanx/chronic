
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

import chronic.app.HtmlChecker;
import chronic.app.StatusRecord;
import chronic.app.StatusRecordMatcher;
import chronic.app.StatusRecordPatterns;
import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import java.util.regex.Matcher;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Patterns;


/**
 *
 * @author evans
 */
public class ChronicTest {

    static Logger logger = LoggerFactory.getLogger(ChronicTest.class);

    String orgDomain = "test.org";
    String orgUnit = "test";
    String commonName = "serverx.test.org";
    CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);

    public ChronicTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void emailHeaderPattern() {
        Assert.assertTrue("X-from: test".matches("^\\S*: .*"));
    }

    @Test
    public void mimicPattern() {
        Assert.assertTrue("https://chronica/mimic".matches(".*\\Wmimic\\W*.*"));
    }

    @Test
    public void nagiosMatches() {
        Assert.assertTrue(StatusRecordMatcher.matches(
                "Service OK - 100ms",
                "Service OK - 200ms"
        ));
        Assert.assertTrue(StatusRecordMatcher.matches(
                "Service OK - 100ms",
                "Service UNKNOWN - 200ms"
        ));
        Assert.assertTrue(StatusRecordMatcher.matches(
                "Service UNKNOWN - 100ms",
                "Service CRITICAL - 200ms"
        ));
        Assert.assertFalse(StatusRecordMatcher.matches(
                "Service OK - 100ms",
                "Service CRITICAL - 100ms"
        ));
        Assert.assertFalse(StatusRecordMatcher.matches(
                "ServiceA OK - 100ms",
                "ServiceB OK - 100ms"
        ));
    }

    @Test
    public void nagiosPatternDash() {
        Matcher matcher = StatusRecordPatterns.NAGIOS.matcher("CRITICAL - no connection");
        Assert.assertTrue(matcher.find());
        Assert.assertTrue(matcher.group(1).equals(""));
        Assert.assertTrue(matcher.group(2).equals("CRITICAL"));
        Assert.assertTrue(matcher.group(3).equals("no connection"));
    }

    @Test
    public void nagiosPatternColon() {
        Matcher matcher = StatusRecordPatterns.NAGIOS.matcher("CRITICAL: no connection");
        Assert.assertTrue(matcher.find());
        Assert.assertTrue(matcher.group(1).equals(""));
        Assert.assertTrue(matcher.group(2).equals("CRITICAL"));
        Assert.assertTrue(matcher.group(3).equals("no connection"));
    }

    @Test
    public void nagiosChanged() {
        Cert cert = new Cert(certKey);
        StatusRecord record1 = new StatusRecord(cert);
        StatusRecord record2 = new StatusRecord(cert);
        record1.getLineList().add("ACME OK - some detail");
        record2.getLineList().add("ACME OK - other detail");
        Assert.assertTrue(record1.matches(record2));
        record2 = new StatusRecord(cert);
        record2.getLineList().add("ACMY OK - other detail");
        Assert.assertFalse(record1.matches(record2));
        record2 = new StatusRecord(cert);
        record2.getLineList().add("ACME CRITICAL - some detail");
        Assert.assertFalse(record1.matches(record2));
    }

    @Test
    public void cronSubject() {
        Matcher matcher = StatusRecordPatterns.CRON_SUBJECT.matcher(
                "Subject: Cron <root@client> ~/scripts/test.sh");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("root", matcher.group(1));
        Assert.assertEquals("client", matcher.group(2));
        Assert.assertEquals("~/scripts/test.sh", matcher.group(3));
    }

    @Test
    public void sanitary() {
        sanitary(true, "<b>hello</b>");
        sanitary(true, "Indeed <b>hello</b>");
        sanitary(true, "<b>hello</b> indeed");
        sanitary(true, "<i>hello</i>");
        sanitary(true, "<h4>title</h4>");
        sanitary(true, "");
        sanitary(false, "<script>alert()</script>");
        sanitary(false, "<p style='expression:call()'>");
    }

    @Test
    public void tagPattern() {
        Assert.assertTrue(Patterns.matchesTag("test test <i>test</i> test"));
        Assert.assertFalse(Patterns.matchesTag("test"));
    }

    private void sanitary(boolean expected, String line) {
        if (HtmlChecker.sanitary(line) != expected) {
            throw new AssertionFailedError(line);
        }
    }

}

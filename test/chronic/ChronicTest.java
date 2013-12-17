/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chronic;

import chronic.app.HtmlChecker;
import chronic.app.StatusRecord;
import chronic.app.StatusRecordPatterns;
import chronic.entitykey.CertKey;
import java.util.regex.Matcher;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vellum.data.Patterns;

/**
 *
 * @author evans
 */
public class ChronicTest {
    
    String orgUrl = "test.org";
    String orgUnit = "test";
    String commonName = "serverx.test.org";
    CertKey certKey = new CertKey(orgUrl, orgUnit, commonName);            
    
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
    public void nagiosPattern() {
        Matcher matcher = StatusRecordPatterns.NAGIOS.matcher("CRITICAL - 101ms");
        Assert.assertTrue(matcher.find());
        Assert.assertTrue(matcher.group(1).equals(""));
        Assert.assertTrue(matcher.group(2).equals("CRITICAL"));
    }
    
    @Test
    public void nagiosChanged() {
        StatusRecord record1 = new StatusRecord(certKey);
        StatusRecord record2 = new StatusRecord(certKey);
        record1.getLineList().add("ACME OK - some detail");
        record2.getLineList().add("ACME OK - other detail");
        Assert.assertTrue(record1.matches(record2));
        record2 = new StatusRecord(certKey);
        record2.getLineList().add("ACMY OK - other detail");
        Assert.assertFalse(record1.matches(record2));
        record2 = new StatusRecord(certKey);
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chronic;

import java.util.regex.Matcher;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author evans
 */
public class ChronicTest {
    
    String orgUrl = "test.org";
    
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
    public void nagiosChanged() {
        StatusRecord record1 = new StatusRecord(orgUrl);
        StatusRecord record2 = new StatusRecord(orgUrl);
        record1.getLineList().add("ACME OK - some detail");
        record2.getLineList().add("ACME OK - other detail");
        Assert.assertTrue(record1.equals(record2));
        record2 = new StatusRecord(orgUrl);
        record2.getLineList().add("ACMY OK - other detail");
        Assert.assertFalse(record1.equals(record2));
        record2 = new StatusRecord(orgUrl);
        record2.getLineList().add("ACME CRITICAL - some detail");
        Assert.assertFalse(record1.equals(record2));
    }
    
    @Test
    public void cronSubject() {
        Matcher matcher = StatusRecordParser.subjectCronPattern.matcher(
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

    private void sanitary(boolean expected, String line) {
        if (HtmlChecker.sanitary(line) != expected) {
            throw new AssertionFailedError(line);
        }
    }
    
}
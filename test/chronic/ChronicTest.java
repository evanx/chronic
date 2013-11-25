/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chronic;

import chronicapp.StatusRecord;
import java.util.regex.Matcher;
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
        StatusRecord record1 = new StatusRecord();
        StatusRecord record2 = new StatusRecord();
        record1.getLineList().add("ACME OK - some detail");
        record2.getLineList().add("ACME OK - other detail");
        Assert.assertFalse("nagiosChanged", record1.isLinesChanged(record2));
        record2 = new StatusRecord();
        record2.getLineList().add("ACMY OK - other detail");
        Assert.assertTrue("nagiosChanged", record1.isLinesChanged(record2));
        record2 = new StatusRecord();
        record2.getLineList().add("ACME CRITICAL - some detail");
        Assert.assertTrue("nagiosChanged", record1.isLinesChanged(record2));
    }
    
    @Test
    public void cronSubject() {
        Matcher matcher = StatusRecord.subjectCronPattern.matcher(
                "Subject: Cron <root@client> ~/scripts/test.sh");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("root", matcher.group(1));
        Assert.assertEquals("client", matcher.group(2));
        Assert.assertEquals("scripts/test", matcher.group(3));
    }
    
}

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

import chronic.alert.HtmlChecker;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Patterns;

/**
 *
 * @author evans
 */
public class HtmlCheckerTest {

    static Logger logger = LoggerFactory.getLogger(HtmlCheckerTest.class);

    public HtmlCheckerTest() {
    }
    
    @Test
    public void sanitary() {
        sanitary(true, "<b>hello</b>");
        sanitary(true, "Indeed <b>hello</b>");
        sanitary(true, "<b>hello</b> indeed");
        sanitary(true, "<i>hello</i>");
        sanitary(true, "<h4>title</h4>");
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

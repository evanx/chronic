
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

import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.SafeDateFormat;

/**
 *
 * @author evans
 */
public class TimeZoneTest {

    static Logger logger = LoggerFactory.getLogger(TimeZoneTest.class);

    @Test
    public void timeZone() {
        String id = String.format("GMT%+03d", 2);
        long timestamp = System.currentTimeMillis();
        logger.info("timestamp {}", timestamp);
        Assert.assertEquals("GMT+02:00", TimeZone.getTimeZone(id).getID().toString());
        Assert.assertTrue(new SafeDateFormat("yyyy-MM-dd HH:mm:ss,SSS Z").format(
                TimeZone.getTimeZone(id), timestamp).endsWith(" +0200"));
    }

}

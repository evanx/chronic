
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

import chronic.alert.MetricSeries;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class ChronicSeriesTest {

    static Logger logger = LoggerFactory.getLogger(ChronicSeriesTest.class);


    public ChronicSeriesTest() {
    }
    
    @Test
    public void test() {
        MetricSeries series = new MetricSeries(10);
        for (int i = 0; i < 20; i++) {
            series.add(System.currentTimeMillis(), 10 + i);
        }
        logger.info("size {}", series.size());
        logger.info("avg {}", series.avg());
        logger.info("map {}", series.getMinutelyMap());
    }
    
}

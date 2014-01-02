
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
package vellum;

import chronic.util.ByteArraySeries;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class ByteArraySeriesTest {

    static Logger logger = LoggerFactory.getLogger(ByteArraySeriesTest.class);


    public ByteArraySeriesTest() {
    }
    
    @Test
    public void test() {
        ByteArraySeries series = new ByteArraySeries(10);
        for (int i = 0; i < 20; i++) {
            series.add(10 + i);
        }
        logger.info("array {}", Arrays.toString(series.getFloatArray()));
        Assert.assertEquals(10, series.size());
        Assert.assertTrue(Float.toString(series.average()).startsWith("24.36"));
        Assert.assertEquals(10, series.getFloatArray().length);
        Assert.assertTrue(series.getFloatArray()[5].toString().startsWith("24.80"));
    }
    
}

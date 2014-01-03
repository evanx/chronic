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
package chronic.check;

import chronic.alert.StatusCheck;
import java.net.Socket;
import vellum.data.Millis;

/**
 *
 * @author evan.summers
 */
public class ClockChecker implements StatusCheck {
    long timestamp;
    
    private ClockChecker(long timestamp) {
        this.timestamp = timestamp;
    }   
        
    @Override
    public String check() {
        long differenceSeconds = Math.abs(System.currentTimeMillis() - timestamp)/1000;
        if (differenceSeconds < 30) {
            return String.format("Clock OK - %d", differenceSeconds);
        } else {
            return String.format("Clock WARNING - %d", differenceSeconds);
        }
    }

    public static ClockChecker parse(String string) {
        return new ClockChecker(Long.parseLong(string));
    }    
}

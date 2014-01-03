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
import chronic.type.StatusType;

/**
 *
 * @author evan.summers
 */
public class NtpChecker implements StatusCheck {

    float seconds;

    private NtpChecker(float seconds) {
        this.seconds = seconds;
    }

    @Override
    public String check() {
        StatusType status = StatusType.OK;
        if (Math.abs(seconds) > 15) {
            status = StatusType.CRITICAL;
        } else if (Math.abs(seconds) > 5) {
            status = StatusType.WARNING;
        }
        return String.format("Ntp %s: %f offset seconds", status, seconds);
    }

    public static NtpChecker parse(String string) {
        return new NtpChecker(Float.parseFloat(string));
    }
}

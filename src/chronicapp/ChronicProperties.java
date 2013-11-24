/*
 * Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronicapp;

import vellum.json.JsonConfig;

/**
 *
 * @author evan.summers
 */
public class ChronicProperties {
    String alertScript = "scripts/alert.sh";
    int periodMinutes = 3;
    boolean testing = false;
    
    public void init(JsonConfig config) {
        alertScript = config.getProperties().getString("alertScript", alertScript);
        periodMinutes = config.getProperties().getInt("periodMinutes", periodMinutes);
        testing = config.getProperties().getBoolean("testing", testing);
    }

    public String getAlertScript() {
        return alertScript;
    }        

    public int getPeriodMinutes() {
        return periodMinutes;
    }        

    public boolean isTesting() {
        return testing;
    }        
}

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

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author evan.summers
 */
public class AlertBuilder {
    static Logger logger = LoggerFactory.getLogger(AlertBuilder.class);
    StringBuilder builder = new StringBuilder();

    public String build(StatusRecord status, StatusRecord previousStatus, AlertRecord previousAlert) 
            throws IOException {
        builder.append(String.format("<b>%s</b>\n", status.getSubject()));
        append(status);
        if (status.getAlertType() == AlertType.CONTENT_CHANGED && previousStatus != null) {
            builder.append("\n<hr><b>Previous:</b>\n");
            append(previousStatus);     
        } else if (status.getAlertType() == AlertType.STATUS_CHANGED && previousAlert != null) {
            builder.append("\n<hr><b>Previous:</b>\n");
            append(previousAlert.getStatusRecord());
        }
        return builder.toString();
    }
    
    private void append(StatusRecord record) {
        for (String line : record.getLineList()) {
            builder.append(line);
            builder.append('\n');
        }
    }    
}

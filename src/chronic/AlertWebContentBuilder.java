/*
 * Source https://github.com/evanx by @evanxsummers

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
package chronic;

import chronic.type.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Patterns;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AlertWebContentBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertWebContentBuilder.class);
    
    String string; 
    
    public String build(AlertRecord alert) {
        if (alert.status.statusType == StatusType.CONTENT_CHANGED) {
            string = Strings.join("\n", alert.status.buildChanged(alert.previousStatus));
        } else {
            string = Strings.join("\n", alert.status.lineList);
        }
        if (alert.status.isHtmlContent()) {
            logger.info("html content");
            return string;
        } else {
            logger.info("preformatted content");
            return String.format("<pre>%s</pre>", string);
        }
    }    
}

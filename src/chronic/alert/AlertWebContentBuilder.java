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
package chronic.alert;

import chronic.type.StatusType;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AlertWebContentBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertWebContentBuilder.class);

    AlertRecord alert;
    String string;

    public void build(AlertRecord alert) {
        this.alert = alert;
        if (new StatusRecordMatcher(alert.status).isHtmlContent()) {
            logger.info("html content");
            alert.htmlContent = String.format("<pre>%s</pre>", 
                    Strings.join("\n", getLineList()));
        } else {
            logger.info("preformatted content");
            alert.preContent =  String.format("%s", 
                    Strings.join("\n", getLineList()));
        }
    }

    private Collection<String> getLineList() {
        if (alert.status.statusType == StatusType.CONTENT_CHANGED) {
            Collection changedLines = alert.status.buildChanged(alert.previousStatus);
            if (!changedLines.isEmpty()) {
                return changedLines;
            }
        }
        return Strings.trimLines(alert.status.lineList);
    }
}

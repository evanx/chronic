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
package chronic.message;

import chronic.alert.AlertMailBuilder;
import chronic.alert.AlertEvent;
import chronic.alert.AlertEventMapper;
import chronic.app.ChronicApp;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.system.Executor;

/**
 *
 * @author evan.summers
 */
public class ChronicScriptMessenger {

    static Logger logger = LoggerFactory.getLogger(ChronicScriptMessenger.class);
    ChronicApp app;

    public ChronicScriptMessenger(ChronicApp app) {
        this.app = app;
    }

    public void init() throws Exception {
        logger.info("initialized");
    }

    public synchronized void alert(AlertEvent alert, TimeZone timeZone) {
        logger.info("alert {}", alert.toString());
        if (app.getProperties().getAlertScript() != null) {
            try {
                Executor executor = new Executor();
                executor.exec(app.getProperties().getAlertScript(),
                        new AlertMailBuilder(app).build(alert, timeZone).getBytes(),
                        new AlertEventMapper(alert, TimeZone.getDefault()).getExtendedMap());
                if (executor.getExitCode() != 0 || !executor.getError().isEmpty()) {
                    logger.warn("process {}: {}", executor.getExitCode(), executor.getError());
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}

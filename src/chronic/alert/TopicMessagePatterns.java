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

import java.util.regex.Pattern;

/**
 *
 * @author evan.summers
 */
public class TopicMessagePatterns {

    public final static Pattern FROM_CRON
            = Pattern.compile("([a-z]+) \\(Cron Daemon\\)$");
    public final static Pattern CRON_SUBJECT
            = Pattern.compile("Cron <(\\w+)@(\\w+)> (.*)");
    public final static Pattern HEADER
            = Pattern.compile("^([A-Z][a-zA-Z-]+):\\s*(.*)\\s*$");
    public final static Pattern LOG
            = Pattern.compile("^(TRACE|DEBUG|INFO|WARN|WARNING|ERROR)[\\s:-]*(.*)$");
    public final static Pattern STATUS
            = Pattern.compile("^(OK|WARNING|CRITICAL|UNKNOWN)[\\s:-]*(.*)$");
    public final static Pattern SERVICE_STATUS
            = Pattern.compile("^(\\S*)\\s*(OK|WARNING|CRITICAL|UNKNOWN)[\\s:-]*(.*)$");
    public final static Pattern HEADER_METRIC_VALUE
            = Pattern.compile("(\\w+)\\s+([+-]?[0-9]+.?,?[0-9]*)");
    public final static Pattern SERVICE_METRIC_VALUE
            = Pattern.compile("([-+]?[0-9]*.[0-9]*)");
    

}

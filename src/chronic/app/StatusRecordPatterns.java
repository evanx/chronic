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
package chronic.app;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class StatusRecordPatterns {

    static Logger logger = LoggerFactory.getLogger(StatusRecordPatterns.class);
    public final static Pattern FROM_CRON
            = Pattern.compile("^From: ([a-z]+) \\(Cron Daemon\\)$");
    public final static Pattern CRON_SUBJECT
            = Pattern.compile("^Subject: Cron <(\\S+)@(\\S+)> (.*)");
    public final static Pattern LOG
            = Pattern.compile("^(TRACE|DEBUG|INFO|WARN|WARNING|ERROR)[\\s:-]*(.*)$");
    public final static Pattern LOG_DEBUG
            = Pattern.compile("^(TRACE|DEBUG)");
    public final static Pattern NAGIOS
            = Pattern.compile("^(\\S*)\\s*(OK|WARNING|CRITICAL|UNKNOWN)[\\s:-]*(.*)$");
    public final static Pattern NAGIOS_UNKNOWN
            = Pattern.compile("^(\\S*)\\s*(UNKNOWN)[\\s:-]*(.*)$");
    public final static Pattern HEADER
            = Pattern.compile("^([a-zA-Z]+):\\s*(.*)$");

}

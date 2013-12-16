package chronic.app;

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


import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Patterns;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class StatusRecordMatcher {

    static Logger logger = LoggerFactory.getLogger(StatusRecordMatcher.class);
    
    StatusRecord status;
    
    public StatusRecordMatcher(StatusRecord status) {
        this.status = status;
    }
    
    public boolean isHtmlContent() {
        for (String line : status.lineList) {
            if (Patterns.matchesTag(line)) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(StatusRecord other) {
        return matches(getFilteredLineList(status), getFilteredLineList(other));
    }
    
    public static List<String> getFilteredLineList(StatusRecord status) {
        List<String> list = new LinkedList();
        for (String line : status.lineList) {
            if (!StatusRecordPatterns.debugLogPattern.matcher(line).matches()) {
                list.add(line);
            }
        }
        return list;
    }
    
    public static boolean matches(List<String> lineList, List<String> otherList) {
        if (lineList.size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < lineList.size(); i++) {
            if (!matches(lineList.get(i), otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean matches(String line, String other) {
        Matcher logMatcher = StatusRecordPatterns.logPattern.matcher(line);
        if (logMatcher.find()) {
            String category = logMatcher.group(1);
            Matcher otherMatcher = StatusRecordPatterns.logPattern.matcher(line);
            if (otherMatcher.find()) {
                String otherCategory = otherMatcher.group(1);
                if (category.equals(otherCategory)) {
                    return Strings.equals(category, "TRACE", "DEBUG", "VERBOSE") ||
                            Strings.equalsIgnoreNumeric(line, other);
                }
            }
            return false;
        } else if (StatusRecordPatterns.headPattern.matcher(line).find()) {
            return true;
        }
        return line.equals(other);
    }
    
}

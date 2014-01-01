
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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class TopicMessageMatcher {

    static Logger logger = LoggerFactory.getLogger(TopicMessageMatcher.class);

    public static boolean matches(TopicMessage message, TopicMessage other) {
        return matches(filterLineList(message.getLineList()), filterLineList(other.getLineList()));
    }

    public static List<String> filterLineList(List<String> lineList) {
        List<String> list = new LinkedList();
        for (String line : lineList) {
            if (TopicMessagePatterns.LOG.matcher(line).matches()) {
            } else if (TopicMessagePatterns.SERVICE_STATUS.matcher(line).matches()) {
                list.add(line);
            } else if (TopicMessagePatterns.STATUS.matcher(line).matches()) {
                list.add(line);
            } else if (TopicMessagePatterns.HEADER.matcher(line).matches()) {
            } else {
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

    public static boolean matches(String line, String otherLine) {
        Matcher matcher = TopicMessagePatterns.LOG.matcher(line);
        if (matcher.find()) {
            String category = matcher.group(1);
            Matcher otherMatcher = TopicMessagePatterns.LOG.matcher(otherLine);
            if (otherMatcher.find()) {
                String otherCategory = otherMatcher.group(1);
                if (category.equals(otherCategory)) {
                    return Strings.equalsIgnoreNumbers(line, otherLine);
                }
            }
            return false;
        }
        matcher = TopicMessagePatterns.SERVICE_STATUS.matcher(line);
        if (matcher.find()) {
            Matcher otherMatcher = TopicMessagePatterns.SERVICE_STATUS.matcher(otherLine);
            if (otherMatcher.find()) {
                return matcher.group(1).equals(otherMatcher.group(1))
                        && statusMatches(matcher.group(2), otherMatcher.group(2));
            }
            return false;
        }
        matcher = TopicMessagePatterns.STATUS.matcher(line);
        if (matcher.find()) {
            Matcher otherMatcher = TopicMessagePatterns.STATUS.matcher(otherLine);
            if (otherMatcher.find()) {
                return statusMatches(matcher.group(1), otherMatcher.group(1));
            }
            return false;
        }
        matcher = TopicMessagePatterns.HEADER.matcher(line);
        if (matcher.find()) {
            return true;
        }
        return line.equals(otherLine);
    }

    private static boolean statusMatches(String status, String otherStatus) {
        return status.equals("UNKNOWN") || otherStatus.equals("UNKNOWN")
                || status.equals(otherStatus);
    }
}

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

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Lists;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class HtmlChecker {

    private static final Logger logger = LoggerFactory.getLogger(HtmlChecker.class);

    private static final String[] names = {"b", "i", "br", "hr", "span",
        "h1", "h2", "h3", "h4", "h5", "h6"
    };
    private static final Collection<String> whitelist = Lists.asHashSet(names);

    public static boolean sanitary(String line) {
        int fromIndex = line.indexOf('<');
        while (fromIndex >= 0 && fromIndex < line.length() - 3) {
            fromIndex++;
            int toIndex = line.indexOf('>', fromIndex);
            if (toIndex > fromIndex) {
                String string = line.substring(fromIndex, toIndex);
                fromIndex = line.indexOf('<', toIndex + 1);
                if (string.charAt(0) == '/') {
                    string = string.substring(1);
                    if (!Strings.equals(string, whitelist)) {
                        logger.warn("not sanitary {}", string);
                        return false;
                    }
                } else if (!Strings.startsWith(string, whitelist)) {
                    logger.warn("not sanitary {}", string);
                    return false;
                }
            } else {
                return false;
            }
        }
        logger.trace("sanitary {}", line);
        return true;
    }
}

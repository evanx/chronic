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
package chronic.check;

import chronic.alert.StatusCheck;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import vellum.data.Patterns;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class HttpChecker implements StatusCheck {
    String address;
    int port;
    int timeout = 4000;
    
    public HttpChecker(String address, int port) {
        this.address = address;
        this.port = port;
    }   
        
    @Override
    public String check() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format("http://%s:%d", address, port));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            String header = connection.getHeaderField(0);
            if (header == null) {
                return String.format("WARNING: %s port %d no http", address, port);
            } else {                    
                return String.format("OK: %s port %d http: %s", address, port, header);
            }
        } catch (IOException e) {
            return String.format("WARNING: %s port %d http error: %s: %s", address, port, 
                    e.getClass().getSimpleName(), e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static HttpChecker parse(String string) {
        String[] fields = string.split("\\s+");
        if (fields.length == 2 && 
                Patterns.matchesDomain(fields[0]) &&
                Patterns.matchesInteger(fields[1])) {
             return new HttpChecker(fields[0], Integer.parseInt(fields[1]));
        }
        throw new IllegalArgumentException(string);
    }
    
}

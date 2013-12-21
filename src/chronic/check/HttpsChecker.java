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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import vellum.data.Patterns;

/**
 *
 * @author evan.summers
 */
public class HttpsChecker implements StatusCheck {
    String address;
    int port;
    int timeout = 4000;
    
    public HttpsChecker(String address, int port) {
        this.address = address;
        this.port = port;
    }   
        
    @Override
    public String check() {
        try (SSLSocket socket = (SSLSocket) 
                SSLContext.getDefault().getSocketFactory().createSocket(address, port)) {
            socket.setSoTimeout(timeout);
            return String.format("OK: %s port %d https: %s", address, port, 
                    socket.getSession().getPeerPrincipal().getName());
        } catch (Exception e) {
            return String.format("WARNING: %s port %d https error: %s", address, port, e.getMessage());
        } 
    }

    public static HttpsChecker parse(String string) {
        String[] fields = string.split("\\s+");
        if (fields.length == 2 && 
                Patterns.matchesDomain(fields[0]) &&
                Patterns.matchesInteger(fields[1])) {
             return new HttpsChecker(fields[0], Integer.parseInt(fields[1]));
        }
        throw new IllegalArgumentException(string);
    }
    
}

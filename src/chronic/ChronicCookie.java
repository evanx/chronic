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

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import vellum.datatype.Millis;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.util.Bytes;

/**
 *
 * @author evan.summers
 */
public class ChronicCookie {
    public static final long MAX_AGE_MILLIS = Millis.fromHours(16);
    
    private final static String path = "/chronicapp";
    private final static String version = "1";
    
    private String email;
    private String label;
    private long loginMillis;
    private String assertion;
    private String authCode; 
            
    public ChronicCookie() {
    }

    public ChronicCookie(JMap map) throws JMapException {
        if (matches(map)) {
            this.email = map.getString("email");
            this.label = map.getString("label");
            this.loginMillis = map.getLong("loginMillis");
            this.assertion = map.getString("assertion");
            this.authCode = map.getString("authCode", null);
        }
    }

    public ChronicCookie(String email, String displayName, long loginMillis, String assertion) {
        this.email = email;
        this.label = displayName;
        this.loginMillis = loginMillis;
        this.assertion = assertion;
    }

    public String getAssertion() {
        return assertion;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getEmail() {
        return email;
    }

    public String getLabel() {
        return label;
    }

    public long getLoginMillis() {
        return loginMillis;
    }

       
    public void validateAuthCode(byte[] secret) throws Exception {
        String code = createAuthCode(secret, email, loginMillis);
        if (!code.equals(code)) {
            throw new Exception("invalid cookied");
        }
    }

    public static String createAuthCode(byte[] secret, String string, long value) 
            throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");
        mac.init(signKey);
        mac.update(string.getBytes());
        return new Base32().encodeAsString(mac.doFinal(Bytes.toByteArray(value)));
    }
    
    public JMap toMap() {
        JMap map = new JMap();
        map.put("email", email);
        map.put("label", label);
        map.put("loginMillis", loginMillis);
        map.put("assertion", assertion);
        map.put("authCode", authCode);
        return map;
    }

    public static JMap emptyMap() {
        JMap map = new JMap();
        map.put("email", "");
        map.put("label", "");
        map.put("loginMillis", 0);
        map.put("authCode", "");
        map.put("assertion", "");
        return map;        
    }
    
    @Override
    public String toString() {
        return toMap().toString();
    }
    
    public static boolean matches(JMap map) {
        return !map.isEmpty("email", "label", "loginMillis", "authCode", "assertion");
    }
}

/*
 * Source https://code.google.com/p/vellum by @evanxsummers

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
import java.util.Collection;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import vellum.datatype.Millis;
import vellum.parameter.StringMap;
import vellum.util.Bytes;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class ChronicCookie {
    public static final long MAX_AGE_MILLIS = Millis.fromHours(16);

    String email;
    String label;
    long loginMillis;
    String accessToken;
    String authCode; 
            
    public ChronicCookie() {
    }

    public ChronicCookie(Map map) {
        this(new StringMap(map));
    }
    
    public ChronicCookie(StringMap map) {
        if (matches(map)) {
            this.email = map.get("email");
            this.label = map.get("label");
            this.loginMillis = map.getLong("loginMillis");
            this.accessToken = map.get("accessToken");
            this.authCode = map.get("authCode");
        }
    }

    public ChronicCookie(String email, String displayName, long loginMillis, String accessToken) {
        this.email = email;
        this.label = displayName;
        this.loginMillis = loginMillis;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
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
    
    public StringMap toMap() {
        StringMap map = new StringMap();
        map.put("email", email);
        map.put("label", label);
        map.put("loginMillis", Long.toString(loginMillis));
        map.put("accessToken", accessToken);
        map.put("authCode", authCode);
        return map;
    }

    public static StringMap emptyMap() {
        StringMap map = new StringMap();
        map.put("email", "");
        map.put("label", "");
        map.put("loginMillis", 0);
        map.put("authCode", "");
        map.put("accessToken", "");
        return map;        
    }
    
    @Override
    public String toString() {
        return toMap().toString();
    }
    
    public static boolean matches(StringMap map) {
        return map.containsKey("email") && 
                map.containsKey("label") &&
                map.containsKey("loginMillis") &&
                map.containsKey("accessToken") &&
                map.containsKey("authCode");
    }

    public static Collection<String> names() {
        return Lists.asList(new String[] {
            "email", "label", "loginMillis", "accessToken", "authCode"
        });
    }    
    
    
}

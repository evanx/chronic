/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package chronic.handler.secure;

import chronic.handler.access.*;
import chronic.api.PlainHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.app.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.enumtype.DelimiterType;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class SecureResolve implements PlainHttpxHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(Resolve.class);
 
    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        Org org = cert.getOrg();
        for (String adminEmail : Strings.split(httpx.getRequestHeader("Admin"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("admin: {}", adminEmail);
            es.persistOrgRole(org, adminEmail, OrgRoleType.ADMIN);
        }
        for (String subscriberEmail : Strings.split(httpx.getRequestHeader("Subscribe"), DelimiterType.COMMA_OR_SPACE)) {
            logger.info("subscriber: {}", subscriberEmail);
            es.persistOrgRole(org, subscriberEmail, OrgRoleType.SUBSCRIBER);
        }
        int port = 443;
        if (org.getServer().equals("localhost")) {
            port = 8444;
        }
        String address = String.format("%s:%d", org.getServer(), port);
        logger.info("server address {}", address);
        return address;
    }
}

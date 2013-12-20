/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package enbase.app;

import chronic.app.ChronicApp;
import chronicexp.jdbc.CertService;
import chronicexp.jdbc.ChronicSchema;
import enbase.api.EnbaseRequest;
import enbase.api.EnbaseActionType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class EmbaseTest {
    static Logger logger = LoggerFactory.getLogger(EmbaseTest.class);
    Long appId = 100001L;
    
    public EmbaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    Server h2Server;
    
    public void init() throws Exception {
        h2Server = Server.createTcpServer().start();
    }

    public void shutdown() {
        if (h2Server != null) {
            h2Server.stop();
        }
    }
    
    @Test
    public void test() {
        Long id = 1L;
        EnbaseRequest tran1 = new EnbaseRequest(appId, "person", EnbaseActionType.ADD);
        tran1.setData("{ label: 'Evan' }");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("chronicPU");
        EntityManager em = emf.createEntityManager();
        em.persist(tran1);
        logger.info("find {}: {}", id, em.find(EnbaseRequest.class, id).getMap());
        id = 2L;
        logger.info("find {}: {}", id, em.find(EnbaseRequest.class, id));
    }
    
}

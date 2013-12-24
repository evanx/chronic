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

import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.Topic;
import javax.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class ChronicEntityService {

    static Logger logger = LoggerFactory.getLogger(ChronicEntityService.class);

    ChronicApp app;
    EntityManagerFactory emf;
    
    public ChronicEntityService(ChronicApp app, EntityManagerFactory emf) {
        this.app = app;
        this.emf = emf;
    }
    
    public Iterable<String> listSubscriberEmails(Topic topic) {
        return emf.createEntityManager().
                createQuery("select s.email from Subscriber s where s.topicId = :topicId").
                setParameter("topicId", topic.getId()).
                getResultList();
    }

    public Iterable<Org> listOrg(String email) 
            throws StorageException {
        return emf.createEntityManager().
                createQuery("select o from Org o join OrgRole r where r.email = :email").
                setParameter("email", email).
                getResultList();
    }
    
    
    public Iterable<Cert> listCerts(String email) throws StorageException {
        return emf.createEntityManager().
                createQuery("select c from Cert c join OrgRole r where r.email = :email").
                setParameter("email", email).
                getResultList();
    }        
}

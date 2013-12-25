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
import chronic.entitykey.CertKey;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class ChronicEntityService {

    static Logger logger = LoggerFactory.getLogger(ChronicEntityService.class);

    EntityManager em;

    public ChronicEntityService() {
    }
    
    public void begin(EntityManager em) {
        this.em = em;
        em.getTransaction().begin();
    }
    
    public void commit() {
        em.getTransaction().commit();
    }
    
    public void rollback() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
    
    public void close() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<String> listSubscriberEmails(Topic topic) {
        return em.createQuery("select s.email from Subscriber s where s.topicId = :topicId").
                setParameter("topicId", topic.getId()).
                getResultList();
    }

    public List<Org> listOrg(String email) {
        return em.createQuery("select o from Org o join OrgRole r"
                + " where r.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public boolean isAdmin(String orgDomain, String email) {
        return em.createQuery("select r.email from Org o join OrgRole r"
                + " where o.orgDomain = :orgDomain").
                setParameter("orgDomain", orgDomain).
                getResultList().contains(email);
    }

    public Cert find(CertKey certKey) {
        return em.createQuery("select c from Cert c join Org o join OrgRole r"
                + " where r.email = :email", Cert.class).
                setParameter("commonName", certKey.getCommonName()).
                setParameter("orgUnit", certKey.getOrgUnit()).
                setParameter("orgDomain", certKey.getOrgDomain()).
                getSingleResult();
    }

    public List<Cert> listCerts(String email) {
        return em.createQuery("select c from Cert c join Org o join OrgRole r"
                + " where r.email = :email").
                setParameter("email", email).
                getResultList();
    }
}

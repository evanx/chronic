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
package chronic.jdbc;

import java.util.Collection;
import java.util.LinkedList;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.AbstractEntity;
import vellum.storage.EntityStore;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public abstract class JdbcStore<E extends AbstractEntity> implements EntityStore<E> {

    Logger logger = LoggerFactory.getLogger(JdbcStore.class);

    DataSource dataSource;

    public JdbcStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void insert(E entity) throws StorageException {
    }

    @Override
    public void update(E entity) throws StorageException {
    }

    @Override
    public boolean containsKey(Comparable key) {
        return false;
    }
    
    @Override
    public void delete(Comparable key) throws StorageException {
    }

    @Override
    public E select(Comparable key) {
        E entity = null;
        return entity;
    }

    @Override
    public E find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        }
        E entity = select(key);
        if (entity == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);           
        }
        return entity;
    }

    public E findId(Long id) throws StorageException {
        E entity = null;
        if (entity == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, id);           
        }
        return entity;
    }
    
    @Override
    public Collection<E> list() {
        Collection list = new LinkedList();
        return list;
    }
}

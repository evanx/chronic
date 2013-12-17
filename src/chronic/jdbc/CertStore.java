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

import chronic.entity.Cert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityStore;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public abstract class CertStore implements EntityStore<Cert> {

    static Logger logger = LoggerFactory.getLogger(CertStore.class);
    static QueryMap queryMap = new QueryMap(CertStore.class);
    DataSource dataSource;
    
    public CertStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void insert(Cert cert) throws StorageException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryMap.get("insert"));
            statement.setString(1, cert.getOrgUrl());
            statement.setString(2, cert.getOrgUnit());
            statement.setString(3, cert.getCommonName());
            statement.setString(4, cert.getEncoded());
            int insertCount = statement.executeUpdate();
            if (insertCount != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            cert.setId(generatedKeys.getLong((1)));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }        
    }

    @Override
    public void update(Cert entity) throws StorageException {
    }

    @Override
    public boolean containsKey(Comparable key) {
        return false;
    }
    
    @Override
    public void delete(Comparable key) throws StorageException {
    }

    @Override
    public Cert select(Comparable key) {
        Cert entity = null;
        return entity;
    }

    @Override
    public Cert find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        }
        Cert entity = select(key);
        if (entity == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);           
        }
        return entity;
    }

    public Cert findId(Long id) throws StorageException {
        Cert entity = null;
        if (entity == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, id);           
        }
        return entity;
    }
    
    @Override
    public Collection<Cert> list() {
        Collection list = new LinkedList();
        try (Connection connection = dataSource.getConnection(); 
                Statement statement = connection.createStatement(); 
                ResultSet resultSet = statement.executeQuery("select * from cert")) {
            while (resultSet.next()) {
                Cert cert = create(resultSet);
                list.add(cert);
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }        
        return list;
    }
    
    private Cert create(ResultSet resultSet) throws SQLException {
        Cert cert = new Cert();
        cert.setId(resultSet.getLong("id"));
        cert.setOrgUrl(resultSet.getString("org_url"));
        cert.setOrgUnit(resultSet.getString("org_unit"));
        cert.setCommonName(resultSet.getString("cn"));
        cert.setEncoded(resultSet.getString("encoded"));
        cert.setEnabled(resultSet.getBoolean("enabled"));
        return cert;
    }
    
}

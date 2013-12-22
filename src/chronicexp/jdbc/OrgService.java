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
package chronicexp.jdbc;

import chronic.entity.Org;
import chronic.entitykey.OrgKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityService;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class OrgService implements EntityService<Org> {

    static Logger logger = LoggerFactory.getLogger(OrgService.class);
    static QueryMap queryMap = new QueryMap(OrgService.class);
    Connection connection;
    
    public OrgService(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement prepare(String queryKey,
            Object... parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(queryMap.get(queryKey));
        int index = 0;
        for (Object parameter : parameters) {
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Org create(ResultSet resultSet) throws SQLException {
        Org org = new Org();
        org.setId(resultSet.getLong("org_id"));
        org.setOrgDomain(resultSet.getString("org_domain"));
        org.setLabel(resultSet.getString("label"));
        org.setEnabled(resultSet.getBoolean("enabled"));
        return org;
    }

    private Collection<Org> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(Org org) throws StorageException {
        try (PreparedStatement statement = prepare("insert")) {
            statement.setString(1, org.getOrgDomain());
            statement.setString(2, org.getLabel());
            statement.setBoolean(3, org.isEnabled());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            org.setId(generatedKeys.getLong(1));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, org.getKey());
        }
    }

    @Override
    public void remove(Comparable key) throws StorageException {
        try (PreparedStatement statement = prepare("delete", key)) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_DELETED, key);
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }
    
    @Override
    public void update(Org org) throws StorageException {
        updateEnabled(org);
    }

    public void updateEnabled(Org org) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled")) {
            statement.setBoolean(1, org.isEnabled());
            statement.setLong(2, org.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, org.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, org.getKey());
        }
    }
    
    public void updateLabel(Org org) throws StorageException {
        try (PreparedStatement statement = prepare("update label")) {
            statement.setString(1, org.getLabel());
            statement.setLong(2, org.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, org.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, org.getKey());
        }
    }

    @Override
    public Org find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        } else if (key instanceof String) {
            return findKey((String) key);
        } else if (key instanceof OrgKey) {
            return findKey(((OrgKey) key).getOrgDomain());
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }

    private Org findId(Long id) throws StorageException {
        try (PreparedStatement statement = prepare("select id", id);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Org org = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return org;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, id);
        }
    }
    
    private Org findKey(String orgDomain) throws StorageException {
        try (PreparedStatement statement = prepare("select key", orgDomain)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                Org org = create(resultSet);
                if (resultSet.next()) {
                    throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, orgDomain);
                }
                return org;
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, orgDomain);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public Org retrieve(Comparable key) throws StorageException {
        Org org = find(key);
        if (org != null) {
            return org;
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND, key);
    }

    @Override
    public Collection<Org> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Org> list(Comparable key) throws StorageException {
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }

}

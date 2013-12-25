/*
 Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.orgRole/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronicexp.jdbc;

import chronic.entity.OrgRole;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.PersonKey;
import chronic.entitytype.OrgRoleType;
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
public class OrgRoleService implements EntityService<OrgRole> {

    static Logger logger = LoggerFactory.getLogger(OrgRoleService.class);
    static QueryMap queryMap = new QueryMap(OrgRoleService.class);
    Connection connection;
    
    public OrgRoleService(Connection connection) {
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

    private OrgRole create(ResultSet resultSet) throws SQLException {
        OrgRole orgRole = new OrgRole();
        orgRole.setId(resultSet.getLong("org_role_id"));
        orgRole.setOrgDomain(resultSet.getString("org_domain"));
        orgRole.setEmail(resultSet.getString("email"));
        orgRole.setRoleType(OrgRoleType.valueOf(resultSet.getString("role_type")));
        orgRole.setEnabled(resultSet.getBoolean("enabled"));
        return orgRole;
    }

    private Collection<OrgRole> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(OrgRole orgRole) throws StorageException {
        try (PreparedStatement statement = prepare("insert")) {
            statement.setString(1, orgRole.getOrgDomain());
            statement.setString(2, orgRole.getEmail());
            statement.setString(3, orgRole.getRoleType().name());
            statement.setBoolean(4, orgRole.isEnabled());
            ResultSet generatedKeys = statement.executeQuery();
            if (!generatedKeys.next()) {
                throw new StorageException(StorageExceptionType.NOT_PERSISTED);
            }
            orgRole.setId(generatedKeys.getLong(1));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, orgRole.getId());
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
    public void update(OrgRole orgRole) throws StorageException {
        updateEnabled(orgRole);
    }

    public void updateEnabled(OrgRole orgRole) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled")) {
            statement.setBoolean(1, orgRole.isEnabled());
            statement.setLong(2, orgRole.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, orgRole.getId());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, orgRole.getId());
        }
    }
    
    @Override
    public OrgRole find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        } else if (key instanceof OrgRoleKey) {
            return findKey((OrgRoleKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }

    private OrgRole findId(Long id) throws StorageException {
        try (PreparedStatement statement = prepare("select id", id);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            OrgRole orgRole = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return orgRole;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, id);
        }
    }
    private OrgRole findKey(OrgRoleKey key) throws StorageException {
        try (PreparedStatement statement = prepare("select key", 
                key.getOrgDomain(), key.getEmail())) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                OrgRole orgRole = create(resultSet);
                if (resultSet.next()) {
                    throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
                }
                return orgRole;
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public OrgRole retrieve(Comparable key) throws StorageException {
        OrgRole orgRole = find(key);
        if (orgRole != null) {
            return orgRole;
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND, key);
    }

    @Override
    public Collection<OrgRole> list(Comparable key) throws StorageException {
        if (key instanceof OrgKey) {
            return list((OrgKey) key);
        } else if (key instanceof PersonKey) {
            return list((PersonKey) key);            
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }
    
    public Collection<OrgRole> list(OrgKey key) throws StorageException {
        try (PreparedStatement statement = prepare("list org", key.getOrgDomain());
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    public Collection<OrgRole> list(PersonKey key) throws StorageException {
        try (PreparedStatement statement = prepare("list email", key.getEmail());
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<OrgRole> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }        
}

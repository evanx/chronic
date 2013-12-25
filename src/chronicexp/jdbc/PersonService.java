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

import chronic.entity.Person;
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
public class PersonService implements EntityService<Person> {

    static Logger logger = LoggerFactory.getLogger(PersonService.class);
    static QueryMap queryMap = new QueryMap(PersonService.class);
    Connection connection;

    public PersonService(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement prepare(String queryKey, Object... parameters) 
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(queryMap.get(queryKey));
        int index = 0;
        for (Object parameter : parameters) {
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Person create(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setLabel(resultSet.getString("label"));
        person.setEmail(resultSet.getString("email"));
        person.setEnabled(resultSet.getBoolean("enabled"));
        return person;
    }

    private Collection<Person> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(Person person) throws StorageException {
        try (PreparedStatement statement = prepare("insert")) {
            statement.setString(1, person.getEmail());
            statement.setString(2, person.getLabel());
            statement.setBoolean(3, person.isEnabled());
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, person.getId());
        }
    }

    @Override
    public void update(Person person) throws StorageException {
        updateEnabled(person);
    }

    public void updateEnabled(Person person) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled", 
                person.isEnabled(), person.getEmail())) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, person.getId());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, person.getId());
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
    public Person find(Comparable key) throws StorageException {
        if (key instanceof String) {
            return findKey((String) key);
        } else {
            throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
        }
    }

    private Person findKey(String key) throws StorageException {
        try (PreparedStatement statement = prepare("select key", key);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Person person = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
            }
            return person;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL,
                    key);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }
    
    @Override
    public Person retrieve(Comparable key) throws StorageException {
        Person person = find(key);
        if (person == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return person;
    }

    @Override
    public Collection<Person> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Person> list(Comparable key) throws StorageException {
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }
}

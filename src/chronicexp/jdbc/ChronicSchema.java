/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronicexp.jdbc;

import chronic.app.ChronicApp;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class ChronicSchema {

    static final int REQUIRED_VERSION_NUMBER = 1;
    static final int CURRENT_VERSION_NUMBER = 1;
    
    static final Logger logger = LoggerFactory.getLogger(ChronicSchema.class);
    
    ChronicApp app;
    DatabaseMetaData databaseMetaData;

    public ChronicSchema(ChronicApp app) {
        this.app = app;
    }

    public void verifySchema() throws SQLException, IOException {
        int version = getSchemaVersion();
        if (version < REQUIRED_VERSION_NUMBER) {
            throw new SQLException("schema version number: " + version);
        }
    }
   
    private int getSchemaVersion() throws SQLException {
        try (Connection connection = app.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "select * from schema_version order by updated_time desc")) {
            databaseMetaData = connection.getMetaData();
            logger.info("databaseProductName " + databaseMetaData.getDatabaseProductName());
            logger.info("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
            logger.info("url " + databaseMetaData.getURL());
            logger.info("userName " + databaseMetaData.getUserName());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("schema version not found");
        }
    }

    private void createSchema() throws SQLException, IOException {
        try (Connection connection = app.getDataSource().getConnection()) {
            String sqlScriptName = getClass().getSimpleName() + ".sql";
            InputStream stream = getClass().getResourceAsStream(sqlScriptName);
            logger.info("sqlScriptName {}", sqlScriptName);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            String sql = new String(bytes);
            String[] sqlStatements = sql.split(";");
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    logger.trace("sqlStatement {}", sqlStatement);
                    try {
                        connection.createStatement().execute(sqlStatement);
                    } catch (SQLException e) {
                        logger.error("create {}", e.getMessage());
                        logger.info("sqlStatement {}", sqlStatement);
                    }
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into schema_version (version_number) values (?)")) {
                preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
                preparedStatement.execute();
            }
        }
    }
}

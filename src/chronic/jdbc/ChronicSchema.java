/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.jdbc;

import chronic.app.ChronicApp;
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

    static final int MIN_VERSION_NUMBER = 0;
    static final int CURRENT_VERSION_NUMBER = 1;
    
    static final Logger logger = LoggerFactory.getLogger(ChronicSchema.class);
    
    ChronicApp app;
    DatabaseMetaData databaseMetaData;

    public ChronicSchema(ChronicApp app) {
        this.app = app;
    }

    public void verifySchema() throws Exception {
        if (MIN_VERSION_NUMBER == 0) {
            createSchema();
        } else if (verifySchemaVersion()) {
        } else {
            createSchema();
        }  
    }

    private boolean verifySchemaVersion() {
        try (Connection connection = app.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                "select * from schema_revision order by update_time desc")) {
            databaseMetaData = connection.getMetaData();
            logger.info("databaseProductName " + databaseMetaData.getDatabaseProductName());
            logger.info("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
            logger.info("url " + databaseMetaData.getURL());
            logger.info("userName " + databaseMetaData.getUserName());
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                int versionNumber = rs.getInt(1);
                return versionNumber >= MIN_VERSION_NUMBER;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    private void createSchema() throws Exception {
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
                    logger.info("sqlStatement {}", sqlStatement);
                    try {
                        connection.createStatement().execute(sqlStatement);
                    } catch (SQLException e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into schema_revision (revision_number) values (?)")) {
                preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
                preparedStatement.execute();
            }
        }
    }
}

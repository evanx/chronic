
package chronic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class TomcatPoolTest {
    Logger logger = LoggerFactory.getLogger(TomcatPoolTest.class);
        
    public TomcatPoolTest() {
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

    @Test
    public void initial() throws Exception {
        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:h2:mem");
        p.setDriverClassName("org.h2.Driver");
        p.setUsername("sa");
        p.setPassword("sa");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(p);
        try (Connection connection = dataSource.getConnection(); 
                Statement statement = connection.createStatement(); 
                ResultSet resultSet = statement.executeQuery("select 1")) {
            while (resultSet.next()) {
                logger.info("{}", resultSet.getObject(1));
            }
        }          
    }    
}
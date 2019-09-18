/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.Data;
import Entity.DatabaseFieldType;
import Entity.SQLData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class DatabaseWriter implements Runnable, MSSQLConnectionInterface{
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    
    private static final String PERSISTENCE_NAME = "MSSQLManager";
    private Map properties;
    private EntityManagerFactory factory;
    private EntityManager manager;

    private DatabaseFieldType type;
    
    private LinkedBlockingQueue<String> queue;
    
    public DatabaseWriter(SQLData data) {
      try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MSSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        String url = "jdbc:sqlserver://" + data.getDbAddress() + ":" + data.getDbPort() + ";databaseName=" + data.getDbName();
        
        properties = new HashMap();
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put(PersistenceUnitProperties.JDBC_URL, url);
        properties.put(PersistenceUnitProperties.JDBC_USER, data.getName());
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, data.getPassword());
        
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME, properties);
        
        manager = factory.createEntityManager();
        
        queue = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Data> readData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setType(DatabaseFieldType type) {
        this.type = type;
    }
    
    public void addData(String data) {
        queue.add(data);
        logger.info("Value added recieved: value={}", data);

    }
}

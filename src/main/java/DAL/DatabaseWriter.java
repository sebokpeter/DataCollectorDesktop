/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.DatabaseFieldType;
import Entity.SQLData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class DatabaseWriter implements Runnable, MSSQLConnectionInterface{
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private final Logger jdbcLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
    
    
    private static final String PERSISTENCE_NAME = "MSSQLManager";
    private Map properties;
    private EntityManagerFactory factory;
    private EntityManager manager;

    private DatabaseFieldType type;
    
    private LinkedBlockingQueue<String> queue;
    
    private boolean terminate = false;
    
    private String query;
    private String tableName;
    private String fieldName;
    
    public DatabaseWriter(SQLData data) {
      try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        jdbcLogger.setLevel(Level.ALL);
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
        query = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, fieldName);

        while (!terminate) {            
            String data = null;
            try {
                data = queue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(DatabaseWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            manager.getTransaction().begin();
            Query q = manager.createNativeQuery(query);
            switch(type) {
                case STRING:
                    saveString(data, q);
                    break;
                case REAL:
                    saveReal(data, q);
                    break;
                case INTEGER:
                    saveInt(data, q);
                    break;
                case BOOLEAN:
                    saveBoolean(data, q);
                    break;
                default:
                    logger.error("Data type not recognized ({}), stopping writer.", type);
                    stop();
            }
            int executeUpdate = q.executeUpdate();
            logger.info("Rows modified: {}", executeUpdate);
            manager.getTransaction().commit();
        }
    }

    public void setType(DatabaseFieldType type) {
        this.type = type;
    }
    
    public void stop() {
        this.terminate = true;
    }
    
    public void addData(String data) {
        queue.add(data);
        logger.info("Value added: {}", data);
    }

    private void saveString(String data, Query q) {
        q.setParameter(1, data);
    }

    private void saveReal(String data,  Query q) {
        double d = Double.parseDouble(data);
        q.setParameter(1, d);
    }

    private void saveInt(String data,  Query q) {
        int d = Integer.parseInt(data);
        q.setParameter(1, d);
    }

    private void saveBoolean(String data,  Query q) {
        boolean b = Boolean.getBoolean(data);
        q.setParameter(1, b);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    
}

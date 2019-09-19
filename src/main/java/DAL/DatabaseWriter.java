/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.DatabaseFieldType;
import Entity.Descriptor;
import Entity.SQLData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
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
    private final Map properties;
    private final EntityManagerFactory factory;
    private final EntityManager manager;
    
    private ConcurrentLinkedHashMap<NodeId, String> data;
    private ConcurrentHashMap<NodeId, Descriptor> descriptors;
    
    private boolean terminate = false;
    
    private String query;
    private String tableName;
    
    public DatabaseWriter(SQLData sqlData) {
      try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        jdbcLogger.setLevel(Level.ALL);
        String url = "jdbc:sqlserver://" + sqlData.getDbAddress() + ":" + sqlData.getDbPort() + ";databaseName=" + sqlData.getDbName();
        
        properties = new HashMap();
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put(PersistenceUnitProperties.JDBC_URL, url);
        properties.put(PersistenceUnitProperties.JDBC_USER, sqlData.getName());
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, sqlData.getPassword());
        
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME, properties);
        
        manager = factory.createEntityManager();
        
        tableName = sqlData.getDc().getTableName();
        
        descriptors = new ConcurrentHashMap<>();
        data = new ConcurrentLinkedHashMap.Builder<NodeId, String>().maximumWeightedCapacity(10000).build();
    }
    
    @Override
    public void run() {

        while (!terminate) {            
            if(data.isEmpty()) {
                continue;
            }
            
            Map.Entry<NodeId, String> entry = data.entrySet().iterator().next();
            NodeId node = entry.getKey();
            String getData = entry.getValue();
            
            data.remove(node);
            
            Descriptor desc = descriptors.get(node);
                        
            String fieldName = desc.getDbField();
            DatabaseFieldType type = desc.getType();
            
            query = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, fieldName);
            
            manager.getTransaction().begin();
            Query q = manager.createNativeQuery(query);
            switch(type) {
                case STRING:
                    saveString(getData, q);
                    break;
                case REAL:
                    saveReal(getData, q);
                    break;
                case INTEGER:
                    saveInt(getData, q);
                    break;
                case BOOLEAN:
                    saveBoolean(getData, q);
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

    
    public void stop() {
        this.terminate = true;
    }
    
    public void addData(NodeId node, String input) {
        data.put(node, input);
        logger.info("Value added: {}", input);
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
    
    public void addDescriptor(NodeId node, Descriptor desc) {
        this.descriptors.put(node, desc);
    }
}

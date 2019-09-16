/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.Data;
import Entity.SQLData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Peter
 */
public class MSSQLConnection implements MSSQLConnectionInterface {

    private static final String PERSISTENCE_NAME = "MSSQLManager";
    private Map properties;
    private EntityManagerFactory factory;
    private EntityManager manager;
    
    
    public MSSQLConnection(SQLData connectionData) {
        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MSSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        String url = "jdbc:sqlserver://" + connectionData.getDbAddress() + ":" + connectionData.getDbPort() + ";databaseName=" + connectionData.getDbName();
        
        properties = new HashMap();
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put(PersistenceUnitProperties.JDBC_URL, url);
        properties.put(PersistenceUnitProperties.JDBC_USER, connectionData.getName());
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, connectionData.getPassword());
        
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME, properties);
        
        manager = factory.createEntityManager();
        
    }
    
    @Override
    public List<Data> readData() {
        return manager.createNamedQuery("Data.findAll").getResultList();
    }
   
}

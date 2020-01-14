package DAL.IO;

import Entity.DatabaseFieldType;
import Entity.Descriptor;
import Entity.SQLData;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.LoggerFactory;

/**
 * Writes data read from an OPC-UA server to MSSQL database.
 *
 * @author Peter
 */
public class DatabaseWriter implements Runnable {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private static final Logger jdbcLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");

    private static final String PERSISTENCE_NAME = "MSSQLManager"; // Persistence name in persistence.xml
    private final Map properties; // Properties for connecting to a database
    private final EntityManagerFactory factory;
    private final EntityManager manager;

    private final ConcurrentLinkedHashMap<NodeId, DataValue> data; // Store data associated with its "origin" NodeId
    private final ConcurrentHashMap<NodeId, Descriptor> descriptors; // Associate NodeIds with their descriptors

    private AtomicBoolean terminate = new AtomicBoolean(false);

    private final String tableName;

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
        data = new ConcurrentLinkedHashMap.Builder<NodeId, DataValue>().maximumWeightedCapacity(10000).build();
        
        logger.info("DatabaseWriter created");
    }

    @Override
    public void run() {
        while (!terminate.get()) {
            if (data.isEmpty()) {
                continue;
            }

            Map.Entry<NodeId, DataValue> entry = data.entrySet().iterator().next(); // Retrieve data entry
            NodeId node = entry.getKey();
            DataValue getData = entry.getValue();

            data.remove(node);

            Descriptor desc = descriptors.get(node);
            
            logger.info("Descriptor: {}", desc.toString());
            
            DatabaseFieldType type = desc.getType();
            // Create query
            String query = String.format("INSERT INTO %s (DESCRIPTOR_ID, NODE_ID, VALUE_TYPE, VALUE, TIMESTAMP) VALUES (%s, '%s', '%s', ?, ?)", tableName, desc.getDId(), desc.getNodeid(), desc.getType().toString());

            manager.getTransaction().begin();
            Query q = manager.createNativeQuery(query);
            
            q.setParameter(1, getData.getValue().getValue().toString());
            q.setParameter(2, new Timestamp(getData.getSourceTime().getJavaTime()));

            int executeUpdate = q.executeUpdate();
            if (executeUpdate != 1) {
                logger.error("Unable to insert value {} - {} to database", getData, type);
            }
            logger.info("Inserted value {} - {} to database", getData, type);
            manager.getTransaction().commit();
        }
    }

    public void stop() {
        logger.info("Stopping database writer");
        this.terminate.set(true);
        manager.close();
    }

    public void addData(NodeId node, DataValue input) {
        data.put(node, input);
        logger.info("Value added: {}", input);
    }

    public void addDescriptor(NodeId node, Descriptor desc) {
        this.descriptors.put(node, desc);
    }
}

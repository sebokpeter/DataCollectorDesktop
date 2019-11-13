package DAL;

import Entity.Descriptor;
import Entity.DescriptorConn;
import Entity.SQLData;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * Responsible for reading SQL configuration information from the H2 database
 *
 * @author Peter
 */
public class SQLConfigurationReader implements SQLConfigurationReaderInterface {

    private static final String PERSISTENCE_NAME = "DataManager";
    private static EntityManagerFactory factory;

    public SQLConfigurationReader() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
    }

    @Override
    public SQLData getConfig(int id) throws Exception {

        EntityManager manager = factory.createEntityManager();

        Query q = manager.createNamedQuery("SQLData.findById").setParameter("id", id);

        List<SQLData> data = q.getResultList();

        if (data.isEmpty()) {
            return null; // We didn't find a configuration with the given ID
        }
        if (data.size() > 1) {
            throw new Exception("Multiple result returned from the database!"); // There should be only one database entry with one given ID
        }

        SQLData sqlData = data.get(0);

        Query q2 = manager.createNamedQuery("DescriptorConn.findByDId").setParameter("dId", sqlData.getDId());

        sqlData.setDc((DescriptorConn) q2.getResultList().get(0));

        Query q3 = manager.createNamedQuery("Descriptor.findByDId").setParameter("dId", sqlData.getDId());

        sqlData.setDescriptors((List<Descriptor>) q3.getResultList());

        return sqlData;
    }

    @Override
    public List<SQLData> getAllConfigs() throws Exception {
        EntityManager manager = factory.createEntityManager();

        Query q = manager.createNamedQuery("Sqldata.findAll");
        List<SQLData> data = q.getResultList();

        return data;
    }

}

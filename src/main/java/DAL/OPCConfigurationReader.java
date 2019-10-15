package DAL;

import Entity.OPCData;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * Responsible for reading SQL configuration information from the H2 database
 * @author Peter
 */
public class OPCConfigurationReader implements OPCConfigurationReaderInterface {
    
    private static final String PERSISTENCE_NAME = "DataManager";
    private static EntityManagerFactory factory;

    public OPCConfigurationReader() throws ClassNotFoundException {
        Class.forName ("org.h2.Driver"); 
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
    }
    
    @Override
    public OPCData getOPCDataByID(int id) throws Exception{
        EntityManager manager = factory.createEntityManager();
        
        Query q = manager.createNamedQuery("OPCData.findById").setParameter("id", id);
        
        List<OPCData> result = q.getResultList();
        
        if(result.isEmpty()) {
            return null;
        }
        
        if(result.size() > 1) {
            throw new Exception("Multiple results returned!");
        }
        
        return result.get(0);

    }

    @Override
    public List<OPCData> getAllOPCData() {
        EntityManager manager = factory.createEntityManager();
        
        Query q = manager.createNamedQuery("OPCData.findAll");
                
        return  q.getResultList();
    }
    
}

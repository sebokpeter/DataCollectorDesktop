package DAL.Interfaces;

import Entity.OPCData;
import java.util.List;

/**
 * Methods that will be used to retrieve OPC configurations from the H2 database
 *
 * @author Peter
 */
public interface OPCConfigurationReaderInterface {

    /**
     * Returns the configuration with the specific ID. If no configuration can
     * be found with the given ID, returns null.
     * 
     * @param id The ID of the configuration.
     * @return The configuration with the specific ID if exists, null otherwise.
     * @throws Exception 
     */
    OPCData getConfigById(int id) throws Exception;

    /**
     * Retrieve all OPC configurations stored in the H2 database.
     *
     * @return A list of OPC configurations
     */
    List<OPCData> getAllConfigs();
    
}

package DAL.Interfaces;

import Entity.SQLData;
import java.util.List;

/**
 *
 * @author Peter
 */
public interface SQLConfigurationReaderInterface {

    /**
     * Returns the configuration with the specific ID.If no configuration can be
     * found with the given ID, returns null.
     *
     * @param id The ID of the configuration.
     * @return The configuration with the specific ID if exists, null otherwise.
     * @throws java.lang.Exception
     */
    SQLData getConfigById(int id) throws Exception;

    /**
     * Retrieve all SQL configurations stored in the H2 database.
     *
     * @return A list of SQL configurations
     * @throws Exception
     */
    List<SQLData> getAllConfigs() throws Exception;
}

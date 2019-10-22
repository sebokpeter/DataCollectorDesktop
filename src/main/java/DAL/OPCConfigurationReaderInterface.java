package DAL;

import Entity.OPCData;
import java.util.List;

/**
 * Methods that will be used to retrieve OPC configurations from the H2 database
 *
 * @author Peter
 */
public interface OPCConfigurationReaderInterface {

    OPCData getOPCDataByID(int id) throws Exception;

    List<OPCData> getAllOPCData();
    
}

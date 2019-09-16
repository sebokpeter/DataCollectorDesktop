/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.OPCData;
import java.util.List;

/**
 * Methods that will be used to retrieve OPC configurations from the H2 database
 * @author Peter
 */
public interface OPCConfigurationReaderInterface {
    
    OPCData getOPCDataByID(int id) throws Exception;
    
    List<OPCData> getAllOPCData();
}

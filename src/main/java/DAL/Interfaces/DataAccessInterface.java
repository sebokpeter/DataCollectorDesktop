/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL.Interfaces;

import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import java.util.List;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 *
 * @author Developer
 */
public interface DataAccessInterface {

    /**
     * Returns the configuration with the specific ID.If no configuration can be
     * found with the given ID, returns null.
     *
     * @param id The ID of the configuration.
     * @return The configuration with the specific ID if exists, null otherwise.
     * @throws Exception
     */
    public SQLData getSQLConfigById(int id) throws Exception;

    /**
     * Retrieve all SQL configurations stored in the H2 database.
     *
     * @return A list of SQL configurations
     * @throws Exception
     */
    public List<SQLData> getAllSQLConfigs() throws Exception;

    /**
     * Returns the configuration with the specific ID. If no configuration can
     * be found with the given ID, returns null.
     * 
     * @param id The ID of the configuration.
     * @return The configuration with the specific ID if exists, null otherwise.
     * @throws Exception 
     */
    public OPCData getOPCConfigById(int id) throws Exception;

    /**
     * Retrieve all OPC configurations stored in the H2 database.
     *
     * @return A list of OPC configurations
     * @throws Exception
     */
    public List<OPCData> getAllOPCConfings() throws Exception;

    
    /**
     * Save data from an OPC node using the DatabaseWriter.
     * @throws Exception 
     */
    public void saveOPCData(NodeId sourceNodeId, DataValue dataValue);

    /**
     * Start a new thread, using a DatabaseWriter Runnable
     */
    public void startDatabaseWriter();
    
    /**
     * Associate a NodeID with its descriptor in the DatabaseWriter
     * @param id 
     * @param desc 
     */
    public void addDescriptor(NodeId id, Descriptor desc);

}

package DAL;

import DAL.IO.DatabaseWriter;
import DAL.IO.OPCConfigurationReader;
import DAL.IO.SQLConfigurationReader;
import DAL.Interfaces.DataAccessInterface;
import DAL.Interfaces.OPCConfigurationReaderInterface;
import DAL.Interfaces.SQLConfigurationReaderInterface;
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
public class DataAccessManager implements DataAccessInterface {

    private final SQLConfigurationReaderInterface _SQLReader;
    private final OPCConfigurationReaderInterface _OPCReader;

    private DatabaseWriter databaseWriter;
    
    private SQLData data;
    
    private static DataAccessManager instance;
    
    private DataAccessManager() throws Exception {
        try {
            _SQLReader = new SQLConfigurationReader();
            _OPCReader = new OPCConfigurationReader();
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }
    
    public DataAccessManager(SQLConfigurationReaderInterface SQLReader, OPCConfigurationReaderInterface OPCReader) {
        _SQLReader = SQLReader;
        _OPCReader = OPCReader;
    }

    
    public static DataAccessManager getInstance() throws Exception {
        if(instance == null) {
            instance = new DataAccessManager();
        }
        
        return instance;
    }
    
    @Override
    public void startDatabaseWriter() {
        databaseWriter = new DatabaseWriter(data);
        Thread dbWriterThread = new Thread(databaseWriter);
        
        dbWriterThread.start();
    }
    
    @Override
    public void addDescriptor(NodeId nodeID, Descriptor desc) {
        databaseWriter.addDescriptor(nodeID, desc);
    }
    
    
    @Override
    public void saveOPCData(NodeId nodeId, DataValue value){
        databaseWriter.addData(nodeId, value);
    }

    @Override
    public SQLData getSQLConfigById(int id) throws Exception {
        if (id > 0) {
            try {
                data = _SQLReader.getConfigById(id);
                return data;
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("ID most be greater than 0!");
        }
    }

    @Override
    public List<SQLData> getAllSQLConfigs() throws Exception {
        try {
            return _SQLReader.getAllConfigs();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public OPCData getOPCConfigById(int id) throws Exception {
        if (id > 0) {
            try {
                return _OPCReader.getConfigById(id);
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("ID most be greater than 0!");
        }
    }

    @Override
    public List<OPCData> getAllOPCConfings() throws Exception {
        try {
            return _OPCReader.getAllConfigs();
        } catch (Exception e) {
            throw e;
        }
    }
}

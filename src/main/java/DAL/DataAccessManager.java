package DAL;

import DAL.IO.OPCConfigurationReader;
import DAL.IO.SQLConfigurationReader;
import DAL.Interfaces.DataAccessInterface;
import DAL.Interfaces.OPCConfigurationReaderInterface;
import DAL.Interfaces.SQLConfigurationReaderInterface;
import Entity.OPCData;
import Entity.SQLData;
import java.util.List;

/**
 *
 * @author Developer
 */
public class DataAccessManager implements DataAccessInterface {

    private final SQLConfigurationReaderInterface _SQLReader;
    private final OPCConfigurationReaderInterface _OPCReader;

    public DataAccessManager() throws Exception {
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

    @Override
    public SQLData getSQLConfigById(int id) throws Exception {
        if (id > 0) {
            try {
                return _SQLReader.getConfigById(id);
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

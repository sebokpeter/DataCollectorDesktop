import DAL.DataAccessManager;
import DAL.IO.DatabaseWriter;
import DAL.IO.OPCConfigurationReader;
import DAL.IO.SQLConfigurationReader;
import Entity.OPCData;
import Entity.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Used to test the DataAccessManager class by mocking the underlying ConfigurationReader classes
 * @author sebok
 */
public class DataAccessManagerTests {
    
    
    @Mock private SQLConfigurationReader sqlConfReaderMock;
    @Mock private OPCConfigurationReader opcConfReaderMock;
    @Mock private DatabaseWriter writer;
    
    private List<SQLData> sqlData;
    private List<OPCData> opcData;
    
    @InjectMocks private DataAccessManager dataAccessManager;
    
    public DataAccessManagerTests() {
                System.err.println("");
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // A list of SQLData objects, serves a mock 'database'
        sqlData = new ArrayList<>();
        sqlData.add(new SQLData(1));
        sqlData.add(new SQLData(2));
        sqlData.add(new SQLData(3));
        sqlData.add(new SQLData(4));
        sqlData.add(new SQLData(4));

        when(sqlConfReaderMock.getAllConfigs()).thenReturn(sqlData);
        
        
        when(sqlConfReaderMock.getConfigById(anyInt())).thenAnswer(new Answer<SQLData>() {
            @Override
            public SQLData answer(InvocationOnMock iom) throws Throwable {
                List<SQLData> filtered = sqlData.stream().filter(d -> Objects.equals(d.getId(), iom.getArgument(0))).collect(Collectors.toList());
                if (filtered.size() == 0) {
                    return null;
                } else if(filtered.size() > 1) {
                    throw new Exception("Multiple result returned from the database!"); 
                } else {
                    return filtered.get(0);
                }
            }
        });
        
        // Set up mock OPC configuration reader
        
        opcData = new ArrayList<>();
        opcData.add(new OPCData(1));
        opcData.add(new OPCData(2));
        opcData.add(new OPCData(3));
        opcData.add(new OPCData(4));
        opcData.add(new OPCData(5));
        
        when(opcConfReaderMock.getAllConfigs()).thenReturn(opcData);
        
        when(opcConfReaderMock.getConfigById(anyInt())).thenAnswer(new Answer<OPCData>() {
            @Override
            public OPCData answer(InvocationOnMock iom) throws Throwable {
                List<OPCData> filtered = opcData.stream().filter(d -> Objects.equals(d.getId(), iom.getArgument(0))).collect(Collectors.toList());
                if (filtered.size() == 0) {
                    return null;
                } else if (filtered.size() > 1) {
                    throw new Exception("Multiple results returned!");
                } else {
                    return filtered.get(0);
                }
            }
        });
        
        dataAccessManager = new DataAccessManager(sqlConfReaderMock, opcConfReaderMock);
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void verifyGetSQLConfigCall() {
        try {
            SQLData result = dataAccessManager.getSQLConfigById(1);
            Mockito.verify(sqlConfReaderMock, Mockito.times(1)).getConfigById(1);
        } catch (Exception ex) {
            Logger.getLogger(DataAccessManagerTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void verifyGetSQLConfigThrowsException() {
        try {
            assertThrows(Exception.class, () -> dataAccessManager.getSQLConfigById(-1)); // Test for exception thrown
            
            SQLData result = dataAccessManager.getSQLConfigById(-1);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "ID most be greater than 0!"); // Test for exception message
        }
    }
    
    
    @Test
    public void verifyGetOPCConfigCall() {
        try {
            OPCData result = dataAccessManager.getOPCConfigById(1);
            Mockito.verify(opcConfReaderMock, Mockito.times(1)).getConfigById(1);
        } catch (Exception ex) {
            Logger.getLogger(DataAccessManagerTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void verifyGetOPCConfigThrowsException() {
        try {
            assertThrows(Exception.class, () -> dataAccessManager.getOPCConfigById(-1));
            
            OPCData result = dataAccessManager.getOPCConfigById(-1);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "ID most be greater than 0!");
        }
    }
    
    @Test
    public void verifyNewThreadCreation() {
        DataAccessManager accessManager = Mockito.spy(new DataAccessManager(sqlConfReaderMock, opcConfReaderMock));
        try {
            accessManager.getSQLConfigById(1);
            dataAccessManager.startDatabaseWriter();
            Mockito.verify(writer).run();

        } catch (Exception ex) {
            Logger.getLogger(DataAccessManagerTests.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}

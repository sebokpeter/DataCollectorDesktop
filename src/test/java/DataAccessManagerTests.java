import DAL.DataAccessManager;
import DAL.IO.OPCConfigurationReader;
import DAL.IO.SQLConfigurationReader;
import Entity.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Used to test the DataAccessManager class by mocking the underlying ConfigurationReader classes
 * @author sebok
 */
public class DataAccessManagerTests {
    
    
    @Mock private SQLConfigurationReader sqlConfReaderMock;
    @Mock private OPCConfigurationReader opcConfReaderMock;

    private List<SQLData> data;
    
    @Mock private DataAccessManager dataAccessManager;
    
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
        data = new ArrayList<>();
        data.add(new SQLData(1));
        data.add(new SQLData(2));
        data.add(new SQLData(3));
        data.add(new SQLData(4));
        data.add(new SQLData(4));

        when(sqlConfReaderMock.getAllConfigs()).thenReturn(data);
        
        
        when(sqlConfReaderMock.getConfigById(anyInt())).thenAnswer(new Answer<SQLData>() {
            @Override
            public SQLData answer(InvocationOnMock iom) throws Throwable {
                List<SQLData> filtered = data.stream().filter(d -> Objects.equals(d.getId(), iom.getArgument(0))).collect(Collectors.toList());
                if (filtered.size() == 0) {
                    return null;
                } else if(filtered.size() > 1) {
                    throw new Exception("Multiple result returned from the database!"); 
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
    public void verifyGetConfigCall() {
        try {
            SQLData result = dataAccessManager.getSQLConfigById(1);
            Mockito.verify(sqlConfReaderMock.getConfigById(0), Mockito.times(1));
        } catch (Exception ex) {
            Logger.getLogger(DataAccessManagerTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

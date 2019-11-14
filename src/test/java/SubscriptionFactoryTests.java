
import BLL.OPC.Subscription.AnonymousSubscription;
import BLL.OPC.Subscription.SubscriptionFactory;
import BLL.OPC.Subscription.SubscriptionInterface;
import BLL.OPC.Subscription.UsernameSubscription;
import Entity.OPCData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Some simple tests for the SubscriptionFactory
 * Since the class is very simple, these tests serve more to gain some knowledge of JUnit and Mockito
 * @author Peter
 */
public class SubscriptionFactoryTests {
    
    private static List<OPCData> anonymousOPCData;
    private static List<OPCData> usernameOPCData;
    private static List<OPCData> incorrectOPCData;
    
    private static final String MOCK_URL = "opc.tcp://localhost:48010";
    private static final String MOCK_USERNAME = "TEST_NAME";
    private static final String MOCK_PW = "Password";
        
    /**
     * @BeforeAll should be used to initialize data, but it does not run
     */
    public SubscriptionFactoryTests() {
        anonymousOPCData = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            anonymousOPCData.add(new OPCData(i, MOCK_URL, Boolean.TRUE, ("Test_Data_Anon_" + i)));
        }
        
        usernameOPCData = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            OPCData data = new OPCData(i, MOCK_URL, Boolean.FALSE, ("TEST_DATA_USERNM_" + i));
            
            data.setUsername(MOCK_USERNAME + "_" + i);
            data.setPassword(MOCK_PW);
            
            usernameOPCData.add(data);      
        }
        
        incorrectOPCData = new ArrayList<>();
        
        for (int i = 1; i <+ 5; i++) {
            incorrectOPCData.add(new OPCData(i, MOCK_URL, Boolean.FALSE, ("TEST_DATA_INCORRECT_" + i)));
        }
    }
    
    @BeforeAll
    public static void setUpClass() {

    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {

    }
    
    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    /**
     * Test if the factory correctly returns anonymous subscriptions 
     */
    @Test
    public void testAnonymous() {
        SubscriptionFactory factory = mock(SubscriptionFactory.class);
        try {
            when(factory.createSubscription(any(OPCData.class))).thenCallRealMethod();
            for (OPCData oPCData : anonymousOPCData) {
                SubscriptionInterface result = null;
                result = factory.createSubscription(oPCData);     

                assertTrue(result instanceof AnonymousSubscription);
        }
        } catch (Exception e) {        
        }
    }
    
    /**
     * Test if the factory correctly returns subscriptions with username and password
     */
    @Test
    public void usernameTest() {
        SubscriptionFactory factory = mock(SubscriptionFactory.class);
        try{
            when(factory.createSubscription(any(OPCData.class))).thenCallRealMethod();
            for (OPCData oPCData : usernameOPCData) {
                SubscriptionInterface result = null;
                result = factory.createSubscription(oPCData);
                assertTrue(result instanceof UsernameSubscription);
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * Test if the factory correctly throws an error if the subscription is not anonymous but no username/password was provided
     */
    @Test
    public void incorrectDataTest() {
        SubscriptionFactory factory = mock(SubscriptionFactory.class);
        try{
            when(factory.createSubscription(any(OPCData.class))).thenCallRealMethod();
            for (OPCData oPCData : usernameOPCData) {
                assertThrows(Exception.class, () -> factory.createSubscription(oPCData));
            }
        } catch (Exception e) {
        }
    }
}

package BLL;

import BLL.OPC.ClientRunner;
import BLL.OPC.Subscription;
import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import java.util.List;

/**
 * Manages subscriptions to an OPC-UA server.
 * @author Peter
 */
public class SubscriptionManager {
    
    private OPCData opcData;
    private SQLData sqlData;

    private Subscription subscription;
    
    
    public SubscriptionManager(OPCData opcData, SQLData sqlData) {
        this.opcData = opcData;
        this.sqlData = sqlData;
        
    }
    
    public void startMonitoring() {
        createSubscriptions();
        
        new ClientRunner(subscription).run();
       
    }

    /**
     * Create a subscription from descriptors. 
     */
    private void createSubscriptions() {
        List<Descriptor> descriptors = sqlData.getDescriptors();
        String url = opcData.getUrl();
        
        if(opcData.getAnon()) {
            subscription = new Subscription(url, true);
        } else {
            String name = opcData.getUsername();
            String pass = opcData.getPassword();
            
            subscription = new Subscription(url, name, pass);
        }
        
        subscription.setData(sqlData);
        subscription.setDescriptions(descriptors);
        
    }
    
}

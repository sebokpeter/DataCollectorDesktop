package BLL;

import BLL.OPC.ClientBase;
import BLL.OPC.ClientRunner;
import BLL.OPC.Subscription.SubscriptionFactory;
import BLL.OPC.Subscription.SubscriptionInterface;
import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import java.util.List;

/**
 * Manages subscriptions to an OPC-UA server.
 *
 * @author Peter
 */
public class SubscriptionManager {

    private final OPCData opcData;
    private final SQLData sqlData;
    private final SubscriptionFactory factory;
    
    public SubscriptionManager(OPCData opcData, SQLData sqlData) {
        this.opcData = opcData;
        this.sqlData = sqlData;
        
        this.factory = new SubscriptionFactory();
    }

    public void startMonitoring() throws Exception {
        SubscriptionInterface sub = createSubscription();

        new ClientRunner((ClientBase) sub).run();
    }

    /**
     * Create a subscription from descriptors.
     */
    private SubscriptionInterface createSubscription() throws Exception {
        List<Descriptor> descriptors = sqlData.getDescriptors();

        SubscriptionInterface subscription = factory.createSubscription(opcData);

        subscription.setDescriptions(descriptors);
        
        return subscription;
    }

}

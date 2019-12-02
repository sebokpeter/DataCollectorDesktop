package BLL.OPC.Subscription;

import Entity.Descriptor;
import Entity.SQLData;
import java.util.List;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 * Interface for subscriptions
 * @author Peter
 */
public interface SubscriptionInterface {
    
    /**
     * Creates NodeId (used for subscribing) from data in a descriptor.
     *
     * @param description Data to create the NodeId
     * @return The created NodeID.
     */
    NodeId createNodeId(Descriptor description);
    
    /**
     * Runs upon receiving a new value
     * @param item The item monitored by this subscription
     * @param dataValue The value received
     */
    void onSubscriptionValue(UaMonitoredItem item, DataValue dataValue);
    
    /**
     * Set the list of descriptors used by this subscription
     * @param descriptions The list of descriptors
     */
    void setDescriptions(List<Descriptor> descriptions);
    
}

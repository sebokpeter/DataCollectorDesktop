package BLL.OPC;

import DAL.DatabaseWriter;
import Entity.Descriptor;
import Entity.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to subscribe an OPC-UA node.
 * @author Peter
 */
public class Subscription extends ClientBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final AtomicLong clientHandles = new AtomicLong(1L);
    
    private List<Descriptor> descriptions;
    private SQLData data;
    
    DatabaseWriter writer;
    
    public Subscription(String url, String username, String password) {
        super(url, username, password);
    }

    public Subscription(String url, boolean anonymousIdentity) {
        super(url, anonymousIdentity);
    }

    public void setData(SQLData data) {
        this.data = data;
    }

    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        
        // Create database writer
        writer = new DatabaseWriter(data);
        
        // Create NodeIds
        List<NodeId> nodeIds = new ArrayList<>();
        
        
        for (Descriptor description : descriptions) {
            nodeIds.add(createNodeId(description));
        }
        
        client.connect().get();
        
        // Create subscription and assign values to read
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000).get();

        List<ReadValueId> readValueIds = new ArrayList<>();
        
        for (NodeId nodeId : nodeIds) {
            readValueIds.add(new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE));
        }
                
        /*UInteger clientHandle = uint(clientHandles.getAndIncrement());
        
        MonitoringParameters parameters = new MonitoringParameters(
                clientHandle,
                1000.0, // Monitoring frequency, maybe add a parameter?
                null,
                uint(10),
                true
        );*/
        
        List<MonitoredItemCreateRequest> requests = new ArrayList<>();
        
        for (ReadValueId readValueId : readValueIds) {
            UInteger clientHandle = uint(clientHandles.getAndIncrement());

            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    1000.0, // Monitoring frequency, maybe add a parameter?
                    null,
                    uint(10),
                    true
            );
            
            requests.add(new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters));
        }
        
        
        BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, pId) -> item.setValueConsumer(this::onSubscriptionValue);
 
        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, requests, onItemCreated).get();
        
        /// Start database writer
        Thread writerThread = new Thread(writer);
        writerThread.start();
        
        // Shutdown hook, probably not working?
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                writer.stop();
            }
        });
        
        for (UaMonitoredItem item : items) {
            if(item.getStatusCode().isGood()) {
                logger.info("item created for nodeID={}", item.getReadValueId().getNodeId());
            } else {
                logger.warn("failed to create item for nodeID={} (status={})", item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }
    }

    
    /**
     * Creates NodeId (used for subscribing) from data in a descriptor.
     * @param description Data to create the NodeId
     * @return The created NodeID.
     */
    private NodeId createNodeId(Descriptor description) {
        NodeId node = null;
        
        String idType = description.getNodeidType();
        String id = description.getNodeid();
        int namespace = description.getNamespace();
        
        if(idType.equals("int")) {
            if (!Utils.Utility.isInteger(id)) {
                logger.error("The ID can not be parsed to an integer! ({})", id);
                throw new IllegalArgumentException("The ID can not be parsed to an integer!");
            }
            
            node = new NodeId(namespace, Integer.parseInt(id));
        } else if (idType.equals("string")) {
            node = new NodeId(namespace, id);
        } else {
            logger.error("ID type is not recognized! ({})", idType);
            throw new IllegalArgumentException("ID type is not recognized! (" + idType + ")");
        }
        
        writer.addDescriptor(node, description);        
        return node;
    }
    
    private void onSubscriptionValue(UaMonitoredItem item, DataValue dataValue) {         
         writer.addData(item.getReadValueId().getNodeId(), dataValue.getValue().getValue().toString());
         logger.info("subscription value recieved: item={} value={}", item.getReadValueId().getNodeId(), dataValue.getValue().getValue());
         
    }

    public void setDescriptions(List<Descriptor> descriptions) {
        this.descriptions = descriptions;
    } 
}

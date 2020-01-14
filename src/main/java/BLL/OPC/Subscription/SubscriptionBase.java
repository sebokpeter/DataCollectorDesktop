package BLL.OPC.Subscription;

import BLL.OPC.ClientBase;
import DAL.DataAccessManager;
import DAL.Interfaces.DataAccessInterface;
import Entity.Descriptor;
import Entity.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.logging.Level;
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
 * Contains the shared methods and fields that are common between the different Subscriptions
 * @author Peter
 */
public abstract class SubscriptionBase extends ClientBase implements SubscriptionInterface {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicLong clientHandles = new AtomicLong(1L);
    private List<Descriptor> descriptions;
    private SQLData data;
    private DataAccessInterface dataAccess;

    public SubscriptionBase(String url, boolean anonymousIdentity) {
        super(url, anonymousIdentity);
        try {
            dataAccess = DataAccessManager.getInstance();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SubscriptionBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SubscriptionBase(String url, String username, String password) {
        super(url, username, password);
        try {
            dataAccess = DataAccessManager.getInstance();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SubscriptionBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {

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

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                logger.info("item created for nodeID={}", item.getReadValueId().getNodeId());
            } else {
                logger.warn("failed to create item for nodeID={} (status={})", item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }    
        
    }

    @Override
    public NodeId createNodeId(Descriptor description) {
        NodeId node = null;

        String idType = description.getNodeidType();
        String id = description.getNodeid();
        int namespace = description.getNamespace();

        switch (idType) {
            case "int":
                if (!Utils.Utility.isInteger(id)) {
                    logger.error("The ID can not be parsed to an integer! ({})", id);
                    throw new IllegalArgumentException("The ID can not be parsed to an integer!");
                }
                node = new NodeId(namespace, Integer.parseInt(id));
                break;
            case "string":
                node = new NodeId(namespace, id);
                break;
            default:
                logger.error("ID type is not recognized! ({})", idType);
                throw new IllegalArgumentException("ID type is not recognized! (" + idType + ")");
        }
        
        dataAccess.addDescriptor(node, description);
        
        return node;
    }

    @Override
    public void onSubscriptionValue(UaMonitoredItem item, DataValue dataValue) {
        dataAccess.saveOPCData(item.getReadValueId().getNodeId(), dataValue);
        logger.info("subscription value recieved: item={} value={}", item.getReadValueId().getNodeId(), dataValue.getValue().getValue());
    }

    @Override
    public void setDescriptions(List<Descriptor> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void setData(SQLData data) {
        this.data = data;
    }
}

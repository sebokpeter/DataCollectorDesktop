/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.OPC;

import DAL.DatabaseWriter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
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

    private int namespace;
    private String id;
    private String idType;
    
    private DatabaseWriter writer;
    
    public Subscription(String url, String username, String password, int ns, String id, String type) {
        super(url, username, password);
        this.namespace = ns;
        this.id = id;
        this.idType = type;
    }

    public Subscription(String url, boolean anonymousIdentity, int ns, String id, String type) {
        super(url, anonymousIdentity);
        this.namespace = ns;
        this.id = id;
        this.idType = type;
    }

    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        NodeId subscribeId = createNodeId();
        
        client.connect().get();
        
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000).get();

        ReadValueId readValueId = new ReadValueId(subscribeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
        
        UInteger clientHandle = uint(clientHandles.getAndIncrement());
        
        MonitoringParameters parameters = new MonitoringParameters(
                clientHandle,
                500.0, // Monitoring frequency, maybe add a parameter?
                null,
                uint(10),
                true
        );
        
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId, MonitoringMode.Reporting, parameters
        );
        
        
        BiConsumer<UaMonitoredItem, Integer> onItemCreated = 
                (item, pId) -> item.setValueConsumer(this::onSubscriptionValue);
        
        
        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, Lists.newArrayList(request), onItemCreated).get();
        
        for (UaMonitoredItem item : items) {
            if(item.getStatusCode().isGood()) {
                logger.info("item created for nodeID={}", item.getReadValueId().getNodeId());
            } else {
                logger.warn("failed to create item for nodeID={} (status={})", item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }
    }

    private NodeId createNodeId() {
        
        NodeId node = null;
        
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
        
        return node;
    }
    
    
    private void onSubscriptionValue(UaMonitoredItem item, DataValue dataValue) {
        logger.info("subscription value recieved: item={} value={}", item.getReadValueId().getNodeId(), dataValue.getValue().getValue());
    }

}

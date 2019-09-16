/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

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
 *
 * @author Peter
 */
public class SubscriptionExample extends ClientExample {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final AtomicLong clientHandles = new AtomicLong(1L);
    
    private static final String EXAMPLE_USERNAME = "user";
    private static final String EXAMPLE_PASSWORD = "8h5%32@!~";

    
    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000).get();
        
        /*ReadValueId readValueId = new ReadValueId(
                Identifiers.Server_ServerStatus_CurrentTime,
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );*/
        
        //start(client, 1);
        
        NodeId nodeId = new NodeId(2, "Dynamic/RandomInt32");
        NodeId nodeId2 = new NodeId(2, "Player/RunState");

        //NodeId nodeId = Identifiers.Server_ServerStatus_CurrentTime;
        
        
        ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);


       // DataValue value = client.readValue(0, TimestampsToReturn.Both, nodeId2).get();
        
       // logger.info("Server state={}", value.getValue());

        
        UInteger clientHandle = uint(clientHandles.getAndIncrement());
        
        MonitoringParameters parameters = new MonitoringParameters(
                clientHandle,
                1000.0,
                null,
                uint(10),
                true
        );
        
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId, MonitoringMode.Reporting, parameters
        );
        
        
        BiConsumer<UaMonitoredItem, Integer> onItemCreated = 
                (item, id) -> item.setValueConsumer(this::onSubscriptionValue);
        
        
        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, Lists.newArrayList(request), onItemCreated).get();
        
        for (UaMonitoredItem item : items) {
            if(item.getStatusCode().isGood()) {
                logger.info("item created for nodeID={}", item.getReadValueId().getNodeId());
            } else {
                logger.warn("failed to create item for nodeID={} (status={})", item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }
        

        Thread.sleep(6000);
        //start(client, 6);
        //value = client.readValue(0, TimestampsToReturn.Both, nodeId2).get();
        //logger.info("Server state={}", value.getValue());
        future.complete(client);
    }
    
    private void onSubscriptionValue(UaMonitoredItem item, DataValue dataValue) {
        logger.info("subscription value recieved: item={} value={}", item.getReadValueId().getNodeId(), dataValue.getValue().getValue());
    }
    

    private StatusCode start(OpcUaClient client, int input) throws InterruptedException, ExecutionException {
        NodeId objectId = new NodeId(2, "PlayerControl");
        NodeId methodId = new NodeId(2, "Player/remote-control(x)");

        CallMethodRequest request = new CallMethodRequest(objectId, methodId, new Variant[]{new Variant(input)});

        StatusCode code = client.call(request).get().getStatusCode();

        return code;
    } 
    
    @Override
    public IdentityProvider getIdentityProvider() {
        //return new UsernameProvider(EXAMPLE_USERNAME, EXAMPLE_PASSWORD);
        return new AnonymousProvider();
    }
}

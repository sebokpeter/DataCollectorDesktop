/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
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
public class Triggering extends ClientExample{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicLong clientHandles = new AtomicLong(1L);

    
    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        start(client, 1);
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();
        
        ReadValueId valueId = new ReadValueId(new NodeId(2, "1"), AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
        
        UaMonitoredItem reportingItem = createMonitoredItem(subscription, valueId, MonitoringMode.Reporting);
        
        ReadValueId valueId2 = new ReadValueId(Identifiers.Server_ServerStatus_CurrentTime, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
        
        UaMonitoredItem samplingItem = createMonitoredItem(subscription, valueId2, MonitoringMode.Sampling);
        
        subscription.addTriggeringLinks(reportingItem, newArrayList(samplingItem)).get();
                
        Thread.sleep(10000);
        start(client, 6);

        future.complete(client);
    }

    private UaMonitoredItem createMonitoredItem(UaSubscription subscription, ReadValueId valueId, MonitoringMode monitoringMode) throws ExecutionException, InterruptedException{
        UInteger clientHandle = uint(clientHandles.getAndIncrement());
        
        MonitoringParameters parameters = new MonitoringParameters(clientHandle, 1000.0, null, uint(10), true);
        
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(valueId, monitoringMode, parameters);
        
        BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, id) -> item.setValueConsumer(this::onSubscriptionValue);
        
        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, newArrayList(request), onItemCreated).get();
        
        return items.get(0);
    }
    
    
        private StatusCode start(OpcUaClient client, int input) throws InterruptedException, ExecutionException {
        NodeId objectId = new NodeId(2, "PlayerControl");
        NodeId methodId = new NodeId(2, "Player/remote-control(x)");

        CallMethodRequest request = new CallMethodRequest(objectId, methodId, new Variant[]{new Variant(input)});

        StatusCode code = client.call(request).get().getStatusCode();

        return code;
    } 
    
        private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        logger.info(
            "subscription value received: item={}, value={}",
            item.getReadValueId().getNodeId(), value.getValue());
    }

}

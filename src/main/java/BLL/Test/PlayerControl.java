/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class PlayerControl extends ClientExample {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        
        start(client, 1);
        
        NodeId nodeId = new NodeId(2, "Player/RunState");
        
        DataValue value = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
        
        logger.info("Playback status={}", value.getValue());
        
        Thread.sleep(5000);
        start(client, 6);
        future.complete(client);
    }
    
    private StatusCode start(OpcUaClient client, int input) throws InterruptedException, ExecutionException {
    NodeId objectId = new NodeId(2, "PlayerControl");
    NodeId methodId = new NodeId(2, "Player/remote-control(x)");

    CallMethodRequest request = new CallMethodRequest(objectId, methodId, new Variant[]{new Variant(input)});

    StatusCode code = client.call(request).get().getStatusCode();

    return code;
} 
    
}

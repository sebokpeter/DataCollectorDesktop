/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class Browser extends ClientExample {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        
        browseNode("", client, Identifiers.RootFolder);
        
        future.complete(client);
    }

    private void browseNode(String indent, OpcUaClient client, NodeId browseRoot) {
        BrowseDescription description = new BrowseDescription(browseRoot, 
                                                            BrowseDirection.Forward, 
                                                            Identifiers.References, true, 
                                                            uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()), 
                                                            uint(BrowseResultMask.All.getValue())
        );
        
        try { 
            BrowseResult browseResult = client.browse(description).get();
            
            List<ReferenceDescription> references = toList(browseResult.getReferences());
            
            for (ReferenceDescription reference : references) {
                logger.info("{} Node={}", indent, reference.getBrowseName().getName());
                
                reference.getNodeId().local().ifPresent(nodeID -> browseNode(indent + " ", client, nodeID));
            }
        } catch (InterruptedException | ExecutionException ex) {
                logger.error("Browsing nodeId={} failed: {}", browseRoot, ex.getMessage(), ex);
        }
    }
    
}

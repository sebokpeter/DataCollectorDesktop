/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class NodeBrowser extends ClientExample {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    
    @Override
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        
        browseNode("", client, Identifiers.RootFolder);
        
        //browseNode("", client, new NodeId(2, "Assets"));
        
        future.complete(client);
    }

    private void browseNode(String indent, OpcUaClient client, NodeId rootFolder) {
        try {
            List<Node> nodes = client.getAddressSpace().browse(rootFolder).get();
            
            for (Node node : nodes) {
                logger.info("{} Node={} -- {}", indent, node.getBrowseName().get().getName(), node.getNodeId().get());
                
                browseNode(indent + " ", client, node.getNodeId().get());
            }
        } catch (Exception ex) {
            logger.error("Browsing NodeId={} failed: {}", rootFolder, ex.getMessage(), ex);
        }

    }
    
}

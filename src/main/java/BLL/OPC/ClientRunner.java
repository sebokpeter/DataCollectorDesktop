package BLL.OPC;

import java.security.Security;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adopted from: https://github.com/eclipse/milo/blob/master/milo-examples/standalone-examples/src/main/java/org/eclipse/milo/examples/client/SecureClientStandaloneRunner.java
 * @author Peter
 */
public class ClientRunner {
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private final String APPLICATION_NAME = "Seacon Test Client";
    
    private final String APPLICATION_URI = "urn:eclipse:milo:examples:client";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
    
    private final ClientBase clientExample;

    public ClientRunner(ClientBase clientExample) {
        this.clientExample = clientExample;
    }
    
    private OpcUaClient createClient() throws Exception {
        //String discoveryURL = clientExample.getDiscoveryUlr();
        
        //String discoveryURL = clientExample.getEndPointURL();
        //logger.info("URL of the discovery endpoint: {}", discoveryURL);
        
        //List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(discoveryURL).get();
        
       /* logger.info("Available endpoints: ");
        for (EndpointDescription endpoint : endpoints) {
            logger.info(endpoint.getEndpointUrl() + " " + endpoint.getSecurityPolicyUri());
        }

        */
       
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(clientExample.getEndpointURL()).get();
        EndpointDescription endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(clientExample.getSecurityPolicy().getUri())).findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));
                
       
        logger.info("Using endpoint: {} [{}, {}]", clientExample.getEndpointURL(), endpoint.getSecurityPolicyUri(), endpoint.getSecurityMode());
        
        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english(APPLICATION_NAME))
                .setApplicationUri(APPLICATION_URI)
                .setCertificate(null)
                .setKeyPair(null)
                .setEndpoint(endpoint)
                .setIdentityProvider(clientExample.getIdentityProvider())
                .setRequestTimeout(uint(5000))
                .build();
        
        return OpcUaClient.create(config);
    }

    private EndpointDescription chooseEndpoint(List<EndpointDescription> endpoints, SecurityPolicy minSecurityPolicy, MessageSecurityMode minMessageSecurityMode) {
        EndpointDescription bestFound = null; 
        SecurityPolicy bestSecurityPolicy = null;
        
        for (EndpointDescription endpoint : endpoints) {
            SecurityPolicy endpintSecurityPolicy;
            
            try {
                endpintSecurityPolicy = SecurityPolicy.fromUri(endpoint.getSecurityPolicyUri());
            } catch (UaException ex) {
                continue;
            }
            
            if(minSecurityPolicy.compareTo(endpintSecurityPolicy) <= 0) {
                if(minMessageSecurityMode.compareTo(endpoint.getSecurityMode()) <= 0) {
                    if(bestFound == null) {
                        bestFound = endpoint;
                        bestSecurityPolicy = endpintSecurityPolicy;
                    } else {
                        if(bestSecurityPolicy.compareTo(endpintSecurityPolicy) <= 0) {
                            bestFound = endpoint;
                            bestSecurityPolicy = endpintSecurityPolicy;
                        }
                    }
                }
            }
        }
        
        if (bestFound == null) {
            throw new RuntimeException("No desired endpoints returned!");
        }
        
        return bestFound;
    }

    public void run() {
        future.whenComplete((client, ex) -> {
            if(client != null) {
                try {
                    client.disconnect().get();
                    Stack.releaseSharedResources();
                    System.exit(0);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error disconnecting: ", e.getMessage(), e);
                }
            } else {
                logger.error("Error: {}", ex.getMessage(), ex);
                Stack.releaseSharedResources();
            }
            
           /* try {
                Thread.sleep(1000);
                System.exit(0);
            } catch (InterruptedException e) {
                logger.error("Interruption exception: ", e);
            }*/
        });
        
        try { 
            OpcUaClient client = createClient();
            
            try {
                clientExample.run(client, future);
//                future.get(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error runnig example: {}", e.getMessage(), e);
                future.complete(client);
            } 
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        try {
            Thread.sleep(999999999);
        } catch (InterruptedException e) {
            logger.error("Error runnig example: {}", e.getMessage(), e);
        }
    }

}

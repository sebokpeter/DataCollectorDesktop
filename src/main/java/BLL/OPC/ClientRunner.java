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
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adopted from:
 * https://github.com/eclipse/milo/blob/master/milo-examples/standalone-examples/src/main/java/org/eclipse/milo/examples/client/SecureClientStandaloneRunner.java
 *
 * @author Peter
 */
public class ClientRunner {

    private final String APPLICATION_NAME = "Seacon Test Client";
    private final String APPLICATION_URI = "urn:eclipse:milo:examples:client";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
    private final ClientBase clientExample;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public ClientRunner(ClientBase clientExample) {
        this.clientExample = clientExample;
    }

    public void run() {
        future.whenComplete((client, ex) -> {
            if (client != null) {
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
        });

        try {
            OpcUaClient client = createClient();

            try {
                clientExample.run(client, future);
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

    private OpcUaClient createClient() throws Exception {
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
    
}

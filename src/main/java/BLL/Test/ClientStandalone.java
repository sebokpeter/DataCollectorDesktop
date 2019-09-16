/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import com.google.common.collect.ImmutableList;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter
 */
public class ClientStandalone extends ClientExample {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private IdentityProvider identityProvider;
    private X509Certificate cert;
    private KeyPair keyPair;
    
    public static void start() {
        ClientStandalone client = new ClientStandalone();
 //       new ClientRunner(client).run();
    }

    public ClientStandalone() {
        Console console = System.console();
       /* if(console == null) {
            logger.error("Could not get console instance!");
            System.exit(1);
        }
        
        char[] keystorePassArray = console.readPassword("Keystore password: ");
        char[] keyPassArray = console.readPassword("Key password: ");
       
  
        char[] keystorePassArray = "Pass".toCharArray();
        char[] keyPassArray = "Pass".toCharArray();
       
       
        try {
            String path = "secrets/opcua.keystore";
            logger.info("Trying to load keyfile from " + path);
            File file = new File(path);
            FileInputStream fs = new FileInputStream(file);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fs, keystorePassArray);
            fs.close();
            
            PrivateKey key = (PrivateKey) ks.getKey("opcua", keyPassArray);
            
            cert = (X509Certificate) ks.getCertificate("opcua");
            
            keyPair = new KeyPair(cert.getPublicKey(), key);
            
            identityProvider = new X509IdentityProvider(cert, key);
            
        } catch (FileNotFoundException ex) {
            logger.error("Keystore file not found!", ex);
            System.exit(1);
        } catch (Exception ex) {
            logger.error("Loading from keystore failed!", ex);
            System.exit(1);
        }
        */
    }
    
    public  IdentityProvider getIdentityProvider() {
        //return identityProvider;
        return new AnonymousProvider();
    }

    @Override
    KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    X509Certificate getClientCertificate() {
        return cert;
    }
    
    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        
        client.connect().get();
                
        NodeId nodeId = new NodeId(2, "Dynamic/RandomInt32");
        
        DataValue value = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
        
        logger.info("Read data first={}", value.getValue());
        

        
      /*  readServerStateAndTime(client).thenAccept(values -> {
        DataValue d1 = values.get(0);
        DataValue d2 = values.get(1);
        DataValue d3 = values.get(2);
            
        logger.info("Succeeded in making a connection.");
        logger.info("State={}", ServerState.from((Integer) d1.getValue().getValue()));
        logger.info("CurrentTime={}", d2.getValue().getValue());
        logger.info("Monitored items={}", d3.getValue().getValue());
        });*/
      
        future.complete(client);

    }
    
    private StatusCode start(OpcUaClient client, int input) throws InterruptedException, ExecutionException {
        NodeId objectId = new NodeId(2, "PlayerControl");
        NodeId methodId = new NodeId(2, "Player/remote-control(x)");
        
        CallMethodRequest request = new CallMethodRequest(objectId, methodId, new Variant[]{new Variant(input)});
       
        StatusCode code = client.call(request).get().getStatusCode();
        
        return code;
    } 
            
            
    private CompletableFuture<List<DataValue>> readServerStateAndTime(OpcUaClient client) {
        List<NodeId> nodeIds = ImmutableList.of(
                Identifiers.Server_ServerStatus_State,
                Identifiers.Server_ServerStatus_CurrentTime,
                Identifiers.Server_GetMonitoredItems
        );
        
        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }
    
}

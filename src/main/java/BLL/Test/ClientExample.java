/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adopted from: https://github.com/eclipse/milo/blob/master/milo-examples/standalone-examples/src/main/java/org/eclipse/milo/examples/client/SecureClientExample.java
 * @author Peter
 */
public abstract class ClientExample {
    private Logger logger = LoggerFactory.getLogger(ClientExample.class);
    
    protected KeyPair keyPair;
    protected X509Certificate certificate;
    
    String getDiscoveryUlr() {
        //return "opc.tcp://localhost:4840/discovery";
        return "opc.tcp://DESKTOP-1USC488:48010";
        //return "OPCUA-Player/discovery/";
    }
    
    String getEndPointURL() {
        //return "opc.tcp://DESKTOP-1USC488:12000/OPCUA-Player";
        return "opc.tcp://milo.digitalpetri.com:62541/milo";
        //return "opc.tcp://opcuaserver.com:48010";
    }
    
    SecurityPolicy getSecurityPolicy() {
        //return SecurityPolicy.Basic256Sha256;
        return SecurityPolicy.None;
    }
    
    MessageSecurityMode getMessageSecurityMode() {
        //return MessageSecurityMode.SignAndEncrypt;
        return MessageSecurityMode.None;
    }
    
    IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }
    
    abstract void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;
    
    X509Certificate getClientCertificate() {
        if(certificate == null) {
            generateSelfSignedCertificate();
        } 
        
        return certificate;
    }
    
    KeyPair getKeyPair() {
        if(keyPair == null) {
            generateSelfSignedCertificate();
        }
        
        return keyPair;
    }
    
    protected void generateSelfSignedCertificate() {
        try {
            keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not generate RSA Key Pair." , ex);
            System.exit(1);
        }
        
        SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
                .setCommonName("OPC TEST CLIENT")
                .setOrganization("Seacon")
                .setOrganizationalUnit("Intern");
        
        try {
            certificate = builder.build();
        } catch (Exception ex) {
            logger.error("Could not build certificate!", ex);
            System.exit(1);
        }
    }
}

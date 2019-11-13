package BLL.OPC;

import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;

/**
 * Base class for OPC-UA client
 *
 * @author Peter
 */
public abstract class ClientBase {

    private final String url;
    private String username;
    private String password;
    private final boolean anonymousIdentity;

    public ClientBase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.anonymousIdentity = false;
    }

    public ClientBase(String url, boolean anonymousIdentity) {
        this.url = url;
        this.anonymousIdentity = anonymousIdentity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndpointURL() {
        if (url == null) {
            throw new NullPointerException("URL is not set!");
        }
        return url;
    }

    public SecurityPolicy getSecurityPolicy() {
        //return SecurityPolicy.Basic256Sha256;
        return SecurityPolicy.None;
    }

    public MessageSecurityMode getMessageSecurityMode() {
        //return MessageSecurityMode.SignAndEncrypt;
        return MessageSecurityMode.None;
    }

    public IdentityProvider getIdentityProvider() {
        if (anonymousIdentity) {
            return new AnonymousProvider();
        } else {
            if (username == null || password == null) {
                throw new NullPointerException("Username or Password has not been provided!");
            }
            return new UsernameProvider(username, password);
        }
    }

    protected abstract void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;

}

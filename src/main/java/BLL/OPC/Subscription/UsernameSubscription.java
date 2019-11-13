package BLL.OPC.Subscription;

/**
 * A subscription that represent access to a server authenticated by a username and a password
 * @author Peter
 */
public class UsernameSubscription extends SubscriptionBase {
    
    public UsernameSubscription(String url, String username, String password) {
        super(url, username, password);
    }
    
}

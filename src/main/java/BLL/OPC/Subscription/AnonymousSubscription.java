package BLL.OPC.Subscription;

/**
 * A subscription that represents anonymous access to a server
 * @author Peter
 */
public class AnonymousSubscription extends SubscriptionBase {
    
    public AnonymousSubscription(String url, boolean anonymousIdentity) {
        super(url, anonymousIdentity);
    }
    
}

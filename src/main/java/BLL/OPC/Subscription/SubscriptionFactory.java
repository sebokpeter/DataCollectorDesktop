package BLL.OPC.Subscription;

import Entity.OPCData;

/**
 * Creates a subscription based on parameters from user input
 * @author Peter
 */
public class SubscriptionFactory {
    
    /**
     * Creates a subscription based on input from user
     * @param data User input (url, username, password, anonymous)
     * @return A subscription
     */
    public SubscriptionInterface createSubscription(OPCData data) throws Exception {
        
        if (data.getAnon()) {
            return new AnonymousSubscription(data.getUrl(), true);
        } else {
            if (data.getUsername().isEmpty() || data.getUsername().isBlank()) {
                throw new Exception("Username is empty!");
            }
            
            if (data.getPassword().isEmpty() || data.getPassword().isBlank()) {
                throw new Exception("Password is empty!");
            }
            return new UsernameSubscription(data.getUrl(), data.getUsername(), data.getPassword());
        }
    }
    
}

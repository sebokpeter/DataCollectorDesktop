/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BLL.OPC.ClientRunner;
import BLL.OPC.Subscription;
import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter
 */
public class SubscriptionManager {
    
    private OPCData opcData;
    private SQLData sqlData;

    private List<Subscription> subscriptions;
    
    public SubscriptionManager(OPCData opcData, SQLData sqlData) {
        this.opcData = opcData;
        this.sqlData = sqlData;
        
        subscriptions = new ArrayList<>();
    }
    
    public void startMonitoring() {
        createSubscriptions();
        
        for (Subscription subscription : subscriptions) {
            new ClientRunner(subscription).run();
        }
       
    }

    private void createSubscriptions() {
        List<Descriptor> descriptors = sqlData.getDescriptors();
        
        String url = opcData.getUrl();
        
        if(opcData.getAnon()) {
            for (Descriptor descriptor : descriptors) {
                Subscription sub = new Subscription(url, true, descriptor.getNamespace(), descriptor.getNodeid(), descriptor.getNodeidType());
                subscriptions.add(sub);
            }
        } else { 
            String name = opcData.getName();
            String pass = opcData.getPassword();
            for (Descriptor descriptor : descriptors) {
                Subscription sub = new Subscription(url, name, pass, descriptor.getNamespace(), descriptor.getNodeid(), descriptor.getNodeidType());
                subscriptions.add(sub);
            }
        }
    }
    
}

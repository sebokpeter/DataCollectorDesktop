/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BLL.OPC.Subscription;
import Entity.DatabaseFieldType;
import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Peter
 */
public class SubscriptionManager {
    
    private OPCData opcData;
    private SQLData sqlData;

    private Subscription subscription;
    
    public SubscriptionManager(OPCData opcData, SQLData sqlData) {
        this.opcData = opcData;
        this.sqlData = sqlData;
    }
    
    public void startMonitoring() {
        createSubscription();
       
    }

    private void createSubscription() {
        Descriptor descriptor = sqlData.getDescriptor();
        
        if (opcData.getAnon()) {
            subscription = new Subscription(opcData.getUrl(), true, descriptor.getNamespace(), descriptor.getNodeid(), descriptor.getNodeidType());
        } else {
            subscription = new Subscription(opcData.getUrl(), opcData.getName(), opcData.getPassword(), descriptor.getNamespace(), descriptor.getNodeid(), descriptor.getNodeidType());
        }
        
        String dbField = descriptor.getDbField();
        DatabaseFieldType type = descriptor.getType();
        
    }
    
}

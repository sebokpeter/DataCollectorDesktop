/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BLL.OPC.ClientRunner;
import BLL.OPC.Subscription;
import DAL.DatabaseWriter;
import Entity.Descriptor;
import Entity.DescriptorConn;
import Entity.OPCData;
import Entity.SQLData;
import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

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
        createSubscriptions();
        
        new ClientRunner(subscription).run();
       
    }

    private void createSubscriptions() {
        List<Descriptor> descriptors = sqlData.getDescriptors();
        String url = opcData.getUrl();
        
        if(opcData.getAnon()) {
            subscription = new Subscription(url, true);
        } else {
            String name = opcData.getUsername();
            String pass = opcData.getPassword();
            
            subscription = new Subscription(url, name, pass);
        }
        
        subscription.setData(sqlData);
        subscription.setDescriptions(descriptors);
        
    }
    
}

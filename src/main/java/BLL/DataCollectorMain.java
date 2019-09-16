/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BLL.Test.Browser;
import BLL.OPC.ClientRunner;
import BLL.Test.ClientStandalone;
import BLL.Test.NodeBrowser;
import BLL.Test.PlayerControl;
import BLL.OPC.Subscription;
import BLL.Test.SubscriptionExample;
import BLL.Test.Triggering;
import DAL.MSSQLConnection;
import DAL.MSSQLConnectionInterface;
import DAL.OPCConfigurationReader;
import DAL.OPCConfigurationReaderInterface;
import DAL.SQLConfigurationReader;
import DAL.SQLConfigurationReaderInterface;
import Entity.Data;
import Entity.Descriptor;
import Entity.OPCData;
import Entity.SQLData;
import Utils.Utility;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Main class (and entry point) of the data collector application
 * @author Peter
 */
public class DataCollectorMain {

    private static SQLConfigurationReaderInterface sqlConfReader;
    private static OPCConfigurationReaderInterface opcConfReader;
    private static MSSQLConnectionInterface dataReader;
    
    public static void main(String[] args) throws Exception {
        
        BasicConfigurator.configure();
        
        //int ids[] = processArgument(args);
        
        int sqlId = 80;//ids[0];
        int opcId = 34;//ids[1];
        
        SQLData data = getSqlData(sqlId);
        OPCData opcData = getOpcData(opcId);
        
        SubscriptionManager manager = new SubscriptionManager(opcData, data);
        
    }

    /**
     * Return the SQL configuration with the given ID.
     * @param id The ID of the configuration.
     * @return The configuration with the given ID.
     */
    private static SQLData getSqlData(int id) throws Exception {
        sqlConfReader = new SQLConfigurationReader();
        
        SQLData data = null;
         
        try {
            data = sqlConfReader.getConfig(id);
        } catch (Exception ex) {
            Logger.getLogger(DataCollectorMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
         
        if(data == null) {
            throw new Exception(String.format("Could not retrieve configuration with the ID of %d", id));
        }
        
        return data;
    }
    
    /**
     * Return the OPC connection configuration with the given ID.
     * @param opcId The ID of the configuration.
     * @return The configuration with the given ID.
     */
    private static OPCData getOpcData(int opcId) throws Exception{
        opcConfReader = new OPCConfigurationReader();
        
        OPCData data = null;
        
        try {
            data = opcConfReader.getOPCDataByID(opcId);
        } catch (Exception ex) {
            Logger.getLogger(DataCollectorMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        if(data == null) {
            throw new Exception(String.format("Could not retrieve configuration with the ID of %d", opcId));
        }
        
        return data;
    }

    /**
     * Retrieve the ID from the passed arguments.
     * @param args The arguments passed to the program on startup.
     * @return The IDs passed to the program.
     */
    private static int[] processArgument(String[] args) {        
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments supplied! (" + args.length + ")");
        }
        
        String idParam = args[0];
        int id;
        
        // Convert the parameter to a number
        if(Utility.isInteger(idParam)) {
           id = Integer.parseInt(idParam);
        } else {
            throw new IllegalArgumentException("Argument must be a number!");
        }
        
        // Check if it is a valid ID
        if(id < 0) {
            throw new IllegalArgumentException("Argument must be bigger or equal to 0!");
        }
        
        System.out.println("ID: " + id);    
        
        String opcParam = args[1];
        int opcid;
        
        if(Utility.isInteger(opcParam)) {
            opcid = Integer.parseInt(opcParam);
        } else {
            throw new IllegalArgumentException("Argument must be a number!");
        }
        
        if(opcid < 0) {
            throw new IllegalArgumentException("Argument must be bigger or equal to 0!");
        }
        
        return new int[] {id, opcid};
        
    }

    private static void getDataFromMssql(SQLData data) {
        dataReader = new MSSQLConnection(data);
        
        for (Data data1 : dataReader.readData()) {
            System.out.println(data1.toString());
        }
    }
}

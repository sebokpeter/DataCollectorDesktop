package BLL;

import DAL.DataAccessManager;
import DAL.Interfaces.DataAccessInterface;
import Entity.OPCData;
import Entity.SQLData;
import Utils.Utility;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Main class (and entry point) of the data collector application
 *
 * @author Peter
 */
public class DataCollectorMain {

    private static DataAccessInterface datamanager;

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        int ids[] = processArgument(args);
        int sqlId = ids[0];
        int opcId = ids[1];
        
       // int sqlId = 1;
       // int opcId = 1;

        datamanager = DataAccessManager.getInstance();
        
        SQLData data = getSqlData(sqlId);
        OPCData opcData = getOpcData(opcId);

        SubscriptionManager manager = new SubscriptionManager(opcData, data);

        manager.startMonitoring();
    }

    /**
     * Return the SQL configuration with the given ID.
     *
     * @param id The ID of the configuration.
     * @return The configuration with the given ID.
     */
    private static SQLData getSqlData(int id) throws Exception {
        SQLData data = null;

        try {
            data = datamanager.getSQLConfigById(id);
        } catch (Exception ex) {
            Logger.getLogger(DataCollectorMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        if (data == null) {
            throw new Exception(String.format("Could not retrieve configuration with the ID of %d", id));
        }

        return data;
    }

    /**
     * Return the OPC connection configuration with the given ID.
     *
     * @param id The ID of the configuration.
     * @return The configuration with the given ID.
     */
    private static OPCData getOpcData(int id) throws Exception {
        OPCData data = null;

        try {
            data = datamanager.getOPCConfigById(id);
        } catch (Exception ex) {
            Logger.getLogger(DataCollectorMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        if (data == null) {
            throw new Exception(String.format("Could not retrieve configuration with the ID of %d", id));
        }

        return data;
    }

    /**
     * Retrieve the ID from the passed arguments.
     *
     * @param args The arguments passed to the program on startup.
     * @return The IDs passed to the program.
     */
    private static int[] processArgument(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments supplied! (" + args.length + ")");
        }

        String sqlParam = args[0];
        int id;

        // Convert the parameter to a number
        if (Utility.isInteger(sqlParam)) {
            id = Integer.parseInt(sqlParam);
        } else {
            throw new IllegalArgumentException("Argument must be a number!");
        }

        // Check if it is a valid ID
        if (id < 0) {
            throw new IllegalArgumentException("Argument must be bigger or equal to 0!");
        }

        System.out.println("ID: " + id);

        String opcParam = args[1];
        int opcid;

        if (Utility.isInteger(opcParam)) {
            opcid = Integer.parseInt(opcParam);
        } else {
            throw new IllegalArgumentException("Argument must be a number!");
        }

        if (opcid < 0) {
            throw new IllegalArgumentException("Argument must be bigger or equal to 0!");
        }

        return new int[]{id, opcid};
    }

}

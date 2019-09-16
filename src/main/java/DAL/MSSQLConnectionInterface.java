/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Entity.Data;
import java.util.List;

/**
 * Responsible for connecting to the MS SQL database
 * @author Peter
 */
public interface MSSQLConnectionInterface {
        
    /**
     * Reads data from the MS SQL database.
     * @return All the records from the table
     */
    public List<Data> readData();
    
}

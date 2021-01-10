/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * =========================================
 * Sample Usage: Retrieval
 * GProperty loGProp = new GProperty("IntegSys");
 * String lsUserName = loGProp.getConfig("UserName");
 *
 * Sample Usage: Saving
 * GProperty loGProp = new GProperty();
 * loGProp.setConfig("ServerName", "192.168.10.240");
 * loGProp.setConfig("Database", "GMC_ISysDBF");
 * loGProp.setConfig("UserName", "_Â");
 * loGProp.setConfig("Password", "m×£�?\"ÎÑr¸>§");
 * loGProp.setConfig("Port", "3306");
 * loGProp.setConfig("ClientID", "GGC_B019");
 * loGProp.setConfig("ComputerID", "XX");
 * loGProp.save("IntegSys");
 * =========================================
 */

package org.rmj.appdriver;

import java.util.Properties;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Simple implementation of the java.util.Properties object.
 * <p>
 * Used by the GRider class in loading the properties needed to load the Application.
 * 
 * @author kalyptus
 */
public class GProperty {
    Properties prop;

    public GProperty(String product){
      prop = new Properties();
      
        try {
            prop.load(new FileInputStream(product + ".properties"));
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

   public GProperty() {
      prop = new Properties();
   }

   public String getConfig(String config){
      return prop.getProperty(config);
   }

   public void setConfig(String config, String value){
      prop.setProperty(config, value);
   }
   
   public void save(String file){
        try {
            prop.store(new FileOutputStream(file + ".properties"), null);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(GProperty.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
}

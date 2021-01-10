/*
 * Object Name : IniFile
 * Author      : Marlon A. Sayson
 * Date Created: 2009-07-11
 * Purpose     : Reads the ini file that contains the configuration. Implements ini4j.
 *               [ini4j] is a simple Java API for handling configuration files in Windows
 *               .ini format. Additionally, the library includes Java Preferences API
 *               implementation based on the .ini file.
 * Modification History
 *  2009-07-11 Kalyptus
 *      Started creating this object.
 */
package org.rmj.appdriver;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * Simple implementation of the org.ini4j.Ini object.
 * <p>
 * Reads the ini file that contains the configuration. Implements ini4j.
 * [ini4j] is a simple Java API for handling configuration files in Windows
 * .ini format. Additionally, the library includes Java Preferences API 
 * implementation based on the .ini file.
 * 
 * @author kalyptus
 */
public class IniFile {
   private String csFileName;
   private Preferences coPrefs = null;

    // Constructor
   public IniFile() throws Exception{
      Preferences loPrefs = new IniPreferences(new Ini(new File("config.ini")));
      csFileName = loPrefs.node("config").get("inifile", "");

      coPrefs = new IniPreferences(new Ini(new File(csFileName)));
   }

   //Returns the path and filename of the configuration file
   public String getFileName(){
      return csFileName;
   }

   public String getValue(String node, String field){
      return coPrefs.node(node).get(field, "");
   }
}

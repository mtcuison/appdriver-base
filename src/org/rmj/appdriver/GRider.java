/*
 * Object Name : GRider
 * Author      : Marlon A. Sayson
 * Date Created: 2009-07-11
 * Purpose     : Application Connectivity and Configuration Manager.
 * Modification History
 * 2009-07-11 Kalyptus
 *     Started creating this object. Object was based on the clsAppDriver object - with minor changes.
 * 2010-07-06 Kalyptus
 *     Added a capability to select a connection type offline/online mode
 *     See methods:
 *         boolean setOnline(boolean a);
 *         boolean isOnLine()
 * -----------
 * getApproval
 * kwikSearch
 * setSysDate()
 * doLogin()
 * unlockUser()
 * =================
 * GRider();
 * LoadEnv(fsProductID);
 *   - LoadIni
 *   - LoadConfig
 *   - setUpDataSource
 * LoadUser()/LogUser();
 * =================
 * GRider(fsProductID);
 *   - LoadEnv(fsProductID)
 *      - LoadIni
 *      - LoadConfig
 *      - setUpDataSource
 * LoadUser()/LogUser();
 * =================
 */

package org.rmj.appdriver;

import java.io.IOException;
import org.rmj.appdriver.constants.UserState;
import org.rmj.appdriver.constants.UserRight;
import org.rmj.appdriver.constants.UserLogState;
import org.rmj.appdriver.constants.UserType;
import org.rmj.appdriver.constants.UserLockState;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import javax.sql.DataSource;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Application Connectivity and Configuration Manager.
 * 
 * @author kalyptus
 * <pre>
 *    Date Created: 2009-07-11
 *    Company     : RMJ Business Solutions
 *    Copyright(c): 2009 and beyond.   
 * </pre>
 * 
 */
public class GRider {
    protected BasicDataSource poDS;
    protected BasicDataSource poDSOnline;
    boolean pbIsOnline = false;
    
    protected Connection poCon;
    
    private static String SIGNATURE = "08220326";
    public static String GQUOTE = "The quick brown fox jumps over the lazy dog near the river bank.";
    public static byte SERIAL[] = {0x31, 0x39, 0x5, 0x18, 0x0, 0xd, 0x29, 0xb, 0x1f, 0x1, 0x36};
    
    //Error Catcher
    private String psErrorMsg = "";
    private String psMessages = "";

    //Database config variables
    private String psDBSrvrMn = null;  // ip address of central server
    private String psDBSrvrNm = null;
    private String psDBNameXX = null;
    private String psDBPassWD = null;
    private String psDBUserNm = null;
    private String psDBPortNo = null;
    private String psClientID = null;
    
    //kalyptus - 2018.04.28 09:06am
    //Enable to determine the encrypting capability
    private int pnHexCrypt = 0;
    
    //Environment variables;
    private String psClientNm = null;
    private String psAddressx = null;
    private String psTownName = null;
    private String psZippCode = null;
    private String psProvName = null;
    private String psTelNoxxx = null;
    private String psFaxNoxxx = null;
    private String psApproved = null;
    private String psProdctID = null;
    private String psProdctNm = null;
    private String psSysAdmin = null;
    private String psNetWarex = null;
    private String psMachinex = null;
    private String psApplPath = null;
    private String psReptPath = null;
    private String psImgePath = null;
    private Date pdSysDatex = null;
    private int pnNetError;
    private String psBranchCd = null;
    private Date pdLicencex = null;

    private String psCompName = null;
    private String psBranchNm = null;
    private String pcWareHous = null;
    private String pcMainOffc = null;
    private String psDBHostNm = null;
    
    private String psUserIDxx = null;
    private String psLogNoxxx = null;
    private String psEmployNo = "";
    private int pnUserLevl = 0;

   /**
    * Initialize the GRider object.
    * <p>
    * Example of how to use the object.
    * <pre>
    * <code>
    * Grider instance = new GRider();
    * if(instance.loadEnv("gRider")){
    *    //Display the form that will allow the user to enter his her credential.
    *    if(!instance.logUser("gRider", "M001050024")){
    *       //inform failure of loading the product info and/or the user info.
    *       System.out.println(instance.getErrMsg());
    *       return false;
    *    }
    *    //what do you intend to do why you want to use the 'Ghost Rider'.
    * }
    * else{
    *    //inform failure of loading the product info
    *    System.out.println(instance.getErrMsg());
    * }
    * </code>
    * </pre>
    */ 
   public GRider(){
      //System.out.println("GRider()"); 
      psErrorMsg = "";
      psMessages = "";
      poDS = null;
      poDSOnline = null;
   }
   
   /**
    * Initialize the GRider object.
    * <p>
    * Example of how to use the object.
    * <pre>
    * <code>
    * Grider instance = new GRider("gRider");
    * if(instance.getErrMsg().isEmpty()){
    *    if(!instance.logUser("gRider", "M001050024")){
    *       //inform failure of loading the product info and/or the user info.
    *       System.out.println(instance.getErrMsg());
    *       return false;
    *    }
    *    //what do you intend to do why you want to use the 'Ghost Rider'.
    * else{
    *    //inform failure of loading the product info
    *    System.out.println(instance.getErrMsg());
    * }
    * </code>
    * </pre>
    * @param fsProductID the product id of the Application/Package.
    * 
    */
   public GRider(String fsProductID){
      //System.out.println("GRider(String fsProductID)"); 
      psErrorMsg = "";
      psMessages = "";
      poDS = null;
      poDSOnline = null;
      loadEnv(fsProductID);
   }
    
   /**
    * Enables the user [fsUserID] to use the Application.
    * <p>
    * Environment/Configuration information are loaded for the product and user.
    * History of user login is stored at xxxSysUserLog for successful login.
    * <p>
    * To successfully log the Application represented by the product id 
    * should be properly licensed and the user id should be an authorized 
    * user of the Application.
    * <p>
    * The method does not have a form that will allow the user to select
    * the Application nor the ability enter the user name and password.
    * Please use GRiderFX to do that.
    * 
    * @param fsProductID   product id of the application to use.
    * @param fsUserID      user id of the logging user.
    * @return              true if product id and user id is valid, false otherwise. 
    * @see #logoutUser()
    * @see #loadUser(String fsProductID, String fsUserID)
    */
   public boolean logUser(String fsProductID, String fsUserID){
      System.out.println("logUser(String fsProductID, String fsUserID)"); 
      boolean lbisLog = false;
      Connection loCon = null;
      Statement loStmt = null;
      ResultSet loRs = null;

      System.out.println(fsUserID);
      
      String lsSQL = "SELECT *" +
                    " FROM xxxSysUser" +
                    " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

      psErrorMsg = "";
      psMessages = "";

      if(!fsProductID.equalsIgnoreCase(psProdctID)){
         loadEnv(fsProductID);
      }
        
      try{
         loCon = doConnect();
         loStmt = loCon.createStatement();
         loRs = loStmt.executeQuery(lsSQL);
         
         System.out.println(lsSQL);
         //Perform initial validation of user
         //if no record
         if(!loRs.next()){
             setErrMsg("Invalid User ID!");
         }
         //user status is suspended
         else if(loRs.getString("cUserStat").equals(UserState.SUSPENDED)){
             setErrMsg("User is currently suspended");
         }
         //user status is local
         else{
             if(loRs.getString("cUserType").equals(UserType.LOCAL)){
                 if(!loRs.getString("sProdctID").equalsIgnoreCase(fsProductID)){
                     setErrMsg("User is not a member of " + fsProductID + " application");
                 }
             }
         }

         //Peform the next batch of checking
         if(getErrMsg().equals("")){
             if(loRs.getInt("nUserLevl") < UserRight.SYSADMIN){
                 //Check if user is currently login
                 if(loRs.getString("cLogStatx").equals(UserLogState.LOGIN)){
                     setErrMsg("Loging to two station simultaneously is not permitted!");
                 }
                 else if(loRs.getString("cLockStat").equals(UserLockState.LOCKED)){
                    setErrMsg("User is currently lock.");
                 }//if(loRs.getString("cLogStatx").equals("1"))
             }//if(loRs.getInt("nUserLevl") < Constant.USER_SYSADMIN)
         }//if(getMessage().equals(""))

         //If everything is okey then
         if(getErrMsg().equals("")){
            psUserIDxx = fsUserID;
            pnUserLevl = loRs.getInt("nUserLevl");
            psEmployNo = loRs.getString("sEmployNo");

            //kalyptus - 2018.03.15 10:02am
            //Create an entry for this log...
            psLogNoxxx = MiscUtil.getNextCode("xxxSysUserLog", "sLogNoxxx", true, poCon, psBranchCd);
            String sql = "INSERT INTO xxxSysUserLog(" + 
                              "  sLogNoxxx" +
                              ", sUserIDxx" +
                              ", dLogInxxx" +
                              ", sProdctID" +
                              ", sComptrNm" +
                        ") VALUES (" +
                               SQLUtil.toSQL(psLogNoxxx) +
                              ", " + SQLUtil.toSQL(psUserIDxx) +
                              ", " + SQLUtil.toSQL(this.getServerDate()) +
                              ", " + SQLUtil.toSQL(psProdctID) + 
                              ", " + SQLUtil.toSQL(psCompName) + ")";
            this.executeUpdate(sql);

            lbisLog = true;
         }//if(getMessage().equals(""))
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
      finally{
         MiscUtil.close(loRs);
         MiscUtil.close(loStmt);
         MiscUtil.close(loCon);
      }
      return lbisLog;
   }

   /**
    * Disables the user from using the Application.
    * <p>
    * Login record stored at xxxSysUserLog is updated to record successful logout.
    * <p>
    * The product id and the user id are stored in the GRider instance during 
    * the successful login and are used everytime that instance is used.
    * Logging out removes these stored value(product id and user id) from the instance 
    * along will all the information associated with it.
    * 
    * @return true if no problem was encountered during the logout, otherwise false.
    * @see #logUser(String fsProductID, String fsUserID)
    * @see #loadUser(String fsProductID, String fsUserID)
    */
   public boolean logoutUser() {
      String sql = "UPDATE xxxSysUserLog" + 
                  " SET dLogOutxx = " + SQLUtil.toSQL(this.getServerDate()) + 
                  " WHERE sLogNoxxx = " + SQLUtil.toSQL(psLogNoxxx) + 
                    " AND sUserIDxx = " + SQLUtil.toSQL(psUserIDxx);
      this.executeUpdate(sql);
      return true;
   }
    
   /*
    *  Load User without logging them
    */
   public boolean loadUser(String fsProductID, String fsUserID){
      boolean lbisLog = false;
      Connection loCon = null;
      Statement loStmt = null;
      ResultSet loRs = null;
      String lsSQL = "SELECT *" +
                     " FROM xxxSysUser" +
                     " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

      if(poDS == null)
          return false;

      try{
         loCon = doConnect();
         loStmt = loCon.createStatement();
         loRs = loStmt.executeQuery(lsSQL);

         //if no record
         if(!loRs.next()){
               setErrMsg("Invalid User ID!");
         }

         //If everything is okey then
         if(getErrMsg().equals("")){
             psUserIDxx = fsUserID;
             pnUserLevl = loRs.getInt("nUserLevl");
             lbisLog = true;
         }//if(getMessage().equals(""))
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(psMessages);
      }
      finally{
         MiscUtil.close(loRs);
         MiscUtil.close(loStmt);
         MiscUtil.close(loCon);
      }
      return lbisLog;
   }

   /**
    * Refrains the user from logging the Application.
    * <p>
    * Usually this method is use to signal that the user is currently 
    * log in and refrain from further logging in.
    * 
    * @param fsUserID   the user id of the user
    * @return           true if user was successfully lock, otherwise false.
    * @see unlockUser
    */
   public boolean lockUser(String fsUserID){
      boolean lbisLog = false;
      Connection loCon = null;
      Statement loStmt = null;
      ResultSet loRs = null;
      String lsSQL = "SELECT *" +
                    " FROM xxxSysUser" +
                    " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

      psErrorMsg = "";
      psMessages = "";

      if(poDS == null)
         return false;

      try{
         loCon = doConnect();
         loStmt = loCon.createStatement();
         loRs = loStmt.executeQuery(lsSQL);

         //Perform initial validation of user
         //if no record
         if(!loRs.next()){
            setErrMsg("Invalid User ID!");
            return false;
         }
         //user status is suspended
         else if(loRs.getString("cUserStat").equals(UserState.SUSPENDED)){
            setErrMsg("User is currently suspended");
            return false;
         }

         if(loRs.getString("cLockStat").equals(UserLockState.UNLOCKED)){
             lsSQL = "UPDATE xxxSysUser SET" +
                        "  cLockStat = " + SQLUtil.toSQL(UserLockState.LOCKED) +
                    " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

             loCon.createStatement().executeUpdate(lsSQL);
             lbisLog = true;
         }//if(loRs.getString("cLogStatx").equals("1"))
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(psMessages);
      }
      finally{
         MiscUtil.close(loRs);
         MiscUtil.close(loStmt);
         MiscUtil.close(loCon);
      }
      return lbisLog;
   }

   /**
    * Re-allows the user from logging to the Application.
    * <p>
    * Usually the method is used to signal a successful log out.
    * 
    * @param fsUserID   the user id of the user
    * @return           true if user was successfully lock, otherwise false.
    * @see lockUser
    */
   public boolean unlockUser(String fsUserID){
      boolean lbisLog = false;
      Connection loCon = null;
      Statement loStmt = null;
      ResultSet loRs = null;
      String lsSQL = "SELECT *" +
                    " FROM xxxSysUser" +
                    " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

      psErrorMsg = "";
      psMessages = "";

      if(poDS == null)
         return false;

      try{
         loCon = doConnect();
         loStmt = loCon.createStatement();
         loRs = loStmt.executeQuery(lsSQL);

         //Perform initial validation of user
         //if no record
         if(!loRs.next()){
               setErrMsg("Invalid User ID!");
               return false;
         }
         //user status is suspended
         else if(loRs.getString("cUserStat").equals(UserState.SUSPENDED)){
            setErrMsg("User is currently suspended");
            return false;
         }

         if(loRs.getString("cLockStat").equals(UserLockState.LOCKED)){
            lsSQL = "UPDATE xxxSysUser SET" +
                        "  cLockStat = " + SQLUtil.toSQL(UserLockState.UNLOCKED) +
                   " WHERE sUserIDxx = " + SQLUtil.toSQL(fsUserID);

            loCon.createStatement().executeUpdate(lsSQL);
            lbisLog = true;
         }//if(loRs.getString("cLogStatx").equals("1"))
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(psMessages);
      }
      finally{
         MiscUtil.close(loRs);
         MiscUtil.close(loStmt);
         MiscUtil.close(loCon);
      }
      return lbisLog;
   }
   
   /**
    * Creates a java.sql.Connection object.
    * <p>
    * The information stored in the instance of the GRider object 
    * are used in connecting to the database server. 
    * <p>
    * The method uses the MiscUtil.getConnection() method in creating the connection.
    * 
    * @return the java.sql.Connection instance.
    */
   public Connection doConnect(){
      Connection loCon = null;
      
      System.out.println("new doConnect()");
      
      try {
         if(psDBPassWD.isEmpty()){
            loCon = MiscUtil.getConnection(psDBSrvrNm, psDBNameXX);
         }
         else{
            if (pbIsOnline){
               System.out.println("Connecting to " + psDBSrvrMn);
               loCon = MiscUtil.getConnection(psDBSrvrMn, psDBNameXX, psDBUserNm, psDBPassWD, psDBPortNo);
            }
            else{
               System.out.println("Connecting to " + psDBSrvrNm);            
               loCon = MiscUtil.getConnection(psDBSrvrNm, psDBNameXX, psDBUserNm, psDBPassWD, psDBPortNo);
            }
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
      }
      return loCon;
   }


   /**
    * Gets the current timestamp of the database server.
    * <p>
    * Uses the instance of the Connection stored in the instance of
    * GRider in extracting the current timestamp.
    * 
    * @return the current timestamp.
    */
   public Timestamp getServerDate(){
      Connection loCon = null;
      if(poCon == null)
         loCon = doConnect();
      else
         loCon = poCon;
      return getServerDate(loCon);
   }
    
   /**
    * Gets the current timestamp of the database server.
    * <p>
    * Uses the instance of the Connection[loCon] in extracting the 
    * current timestamp.
    * 
    * @param loCon   the connection instance to used.
    * @return the current timestamp.
    */
    public Timestamp getServerDate(Connection loCon){
        ResultSet loRS = null;
        Timestamp loTimeStamp = null;
        String lsSQL = "";

        psErrorMsg = "";
        psMessages = "";

        try{
            if(loCon == null){
                setErrMsg(psErrorMsg);
                return loTimeStamp;
            }

            if(loCon.getMetaData().getDriverName().equalsIgnoreCase("SQLite JDBC")){
                lsSQL = "SELECT DATETIME('now','localtime')";
                
                loRS = loCon.createStatement()
                     .executeQuery(lsSQL);
                //position record pointer to the first record
                loRS.next();
                //assigned timestamp

                loTimeStamp = Timestamp.valueOf(loRS.getString(1));
            }else{
                //assume that default database is MySQL ODBC
                lsSQL = "SELECT SYSDATE()";
                
                loRS = loCon.createStatement()
                    .executeQuery(lsSQL);
                //position record pointer to the first record
                loRS.next();
                //assigned timestamp
                loTimeStamp = loRS.getTimestamp(1);
            }            
        }
        catch(SQLException ex){
            ex.printStackTrace();
            setErrMsg(ex.getSQLState());
        } finally{
            MiscUtil.close(loRS);
        }
        return loTimeStamp;
    }

   /**
    * Sets the last successful usage date of the Application.
    * <p>
    * Most recent usage date are stored at xxxSysApplication. 
    * Thru this information we can restrict the user from regressing the date.
    * 
    * @param foUserID   the currently login user
    * @param foNewDate  the date to set as the most recent usage date.
    * @return           true if update of table is successful, otherwise false;
    */
   public boolean setSystemDate(String foUserID, Date foNewDate){
      String lsSQL = "UPDATE xxxSysApplication SET" +
                   "  dSysDatex = " + SQLUtil.toSQL(foNewDate) +
                   ", sModified = " + SQLUtil.toSQL(foUserID) +
            " WHERE sClientID = " + SQLUtil.toSQL(psClientID) +
              " AND sProdctID = " + SQLUtil.toSQL(psProdctID);

      beginTrans();
      int lnRecord = executeQuery(lsSQL, "xxxSysApplication", "", "");
      if(lnRecord > 0)
         commitTrans();
      else
         rollbackTrans();

      return lnRecord > 0 ? true : false;
   }

   /**
    * Loads all the necessary environment variables needed to use the GRider instance.
    * <p>
    * Steps perform:
    * 1. Loads the configuration file.
    * 2. Loads the remaining configuration information.
    * 
    * @param fsProductID product id of the application to use.  
    * @return            true if successfully loads the necessary configuration info, otherwise false. 
    */
   public boolean loadEnv(String fsProductID){
      //System.out.println("loadEnv(String fsProductID)");
      if(loadConfig(fsProductID)){
         if(psDBPassWD.isEmpty()){
            poDS = (BasicDataSource)setupDataSource(psDBSrvrNm, psDBNameXX);
            System.out.println("loadConfig(lsCompName, fsProductID, psClientID)");
         }
         else{
            poDS = (BasicDataSource)setupDataSource(psDBSrvrNm, psDBNameXX,  psDBUserNm, psDBPassWD, psDBPortNo);
            System.out.println("loadConfig(lsCompName, fsProductID, psClientID)");
         }

         String lsCompName = MiscUtil.getPCName();
         if(loadConfig(lsCompName, fsProductID, psClientID)){
            //System.out.println("Dito"); 
            return true;
         }
      }
      poDS = null;
      return false;
   }

   /*
    * Load the configuration properties from the table
    */
   private boolean loadConfig(String fsCompName, String fsProductID, String fsClientID){
      //System.out.println("loadConfig(String fsCompName, String fsProductID, String fsClientID)");
      PreparedStatement loPstmt = null;
      ResultSet loRs = null;
      Connection loCon = null;
      boolean isOkey = false;
      try{
         poCon = doConnect();
         
         //mac 2020.11.09
         if(poCon.getMetaData().getDriverName().equalsIgnoreCase("SQLite JDBC")) return true;

         loPstmt = poCon.prepareStatement(getSQ_LoadConfig());
         System.out.println(fsCompName);
         loPstmt.setString(1, fsCompName);
         loPstmt.setString(2, fsClientID);
         loPstmt.setString(3, fsProductID);
         loRs = loPstmt.executeQuery();
            
//         System.out.println("Branch inside CompName: " + fsCompName);
//         System.out.println("Branch inside ClientID: " + fsClientID);
//         System.out.println("Branch inside ProductID: " + fsProductID);                        
//         //System.out.println(loRs.getString("sDBHostNm").trim());
//         System.out.println(HostName());
            
         //Test for loaded record
         if(!loRs.next()){
            System.out.println("Application is Not Registered!!!");
            setErrMsg("Application is Not Registered!!!");
            //return false;
         }
         //Test for the value of computer name
         else if(loRs.getString("sComptrNm").isEmpty()){
            System.out.println("Computer is Not Registered to Use The Selected Sytem?");
            setErrMsg("Computer is Not Registered to Use The Selected Sytem");
            //return false;
         }
         //Perform other testing!
         else{
            //Test for number of errors in this system
            //System.out.println("Are we here?");
            pnNetError = loRs.getInt("nNetError");
            //If errors less than 200
//                if(pnNetError < 200){
//                    if(pnNetError >= 100){
//                        setMessage("Application has Reached 100 Application error! " +
//                                  "Please Inform the GCC-SEG for this Application to Avoid Further Damages!");
//                    }
                //Test Signature
            if(!isSignatureOk(loRs.getString("sMachineX"), loRs.getString("sNetWareX"), loRs.getString("sSysAdmin"))){
               System.out.println("Unregistered Copy of " + fsProductID + " detected?");
               setErrMsg("Unregistered Copy of " + fsProductID + " detected!" );
               //return false;
            }
                    //kalyptus - 2015.01.16 10:01am
                    //Make sure that we are connected to the main server
                    // To accomplish this make sure that all Branch_Others->sDBHostNm are properly set.
            else if(!loRs.getString("sDBHostNm").trim().equalsIgnoreCase(HostName())) {
               System.out.println("You are not connected to the designated main server of your branch! \r\n " +
                          "Please Inform the GGC SEG/SSG to assist you with this problem!");
               setErrMsg("You are not connected to the designated main server of your branch! \r\n " +
                          "Please Inform the GGC SEG/SSG to assist you with this problem!");
               //return false;
            }
            else{
               //Well it passes all our test, so load all configs here!
               psClientID = loRs.getString("sClientID");
               psClientNm = loRs.getString("sClientNm");
               psAddressx = loRs.getString("sAddressx");
               psTownName = loRs.getString("sTownName");
               psZippCode = loRs.getString("sZippCode");
               psProvName = loRs.getString("sProvName");
               psTelNoxxx = loRs.getString("sTelNoxxx");
               psFaxNoxxx = loRs.getString("sFaxNoxxx");
               psApproved = loRs.getString("sApproved");
               psBranchCd = loRs.getString("sBranchCd");
               psProdctID = loRs.getString("sProdctID");
               psProdctNm = loRs.getString("sProdctNm");
               psApplPath = loRs.getString("sApplPath");
               psReptPath = loRs.getString("sReptPath");
               psImgePath = loRs.getString("sImgePath");
               psSysAdmin = loRs.getString("sSysAdmin");
               psNetWarex = loRs.getString("sNetWarex");
               psMachinex = loRs.getString("sMachinex");
               pdSysDatex = SQLUtil.toDate(loRs.getString("dSysDatex"), "yyyy-MM-dd HH:mm:ss") ;
               pdLicencex = SQLUtil.toDate(loRs.getString("dLicencex"), "yyyy-MM-dd") ;
               pnNetError = loRs.getInt("nNetError");
               psCompName = loRs.getString("sComptrNm");
               psBranchNm = loRs.getString("sBranchNm");
               pcWareHous = loRs.getString("cWareHous");
               pcMainOffc = loRs.getString("cMainOffc");
               psDBHostNm = loRs.getString("sDBHostNm");    
               isOkey = true;
            }
//                }
//                else if(pnNetError > 200){
//                    setErrMsg("Error Limit has been Reached!  The Application will Locked to Avert Further Damages!");
//                }
         }
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
      finally{
         MiscUtil.close(loRs);
         MiscUtil.close(loPstmt);
     }
     
     //System.out.println("Dito ako" + isOkey); 
     return isOkey;
   }

   private boolean isSignatureOk(String fsMachineX, String fsNetWareX, String fsSysAdmin){
      PreparedStatement loPstmt = null;
      ResultSet loRs = null;
      boolean hasNetWare = false;
      
      //temporarily remove checking of signature;
      if(pnHexCrypt != 1)
         if(true) return true;

      String lsMachineX = this.Decrypt(fsMachineX);
      String lsNetWareX = this.Decrypt(fsNetWareX);
      String lsSysAdmin = this.Decrypt(fsSysAdmin);
      
      //System.out.println("Machine:" + lsMachineX);
      //System.out.println("Net:" + lsNetWareX);
      //System.out.println("Admin:" + lsSysAdmin);
      
      if (lsMachineX.equals(SIGNATURE)){
         try{
            //Open the recordset
            loPstmt = doConnect().prepareStatement(getSQ_Signature());
            loPstmt.setString(1, lsMachineX);
            loPstmt.setString(2, lsNetWareX);
            loRs = loPstmt.executeQuery();
            //System.out.println("Recordcount: " + MiscUtil.RecordCount(loRs));
            //Contains the machine and netware account?
            if(MiscUtil.RecordCount(loRs) == 2) {
               //search for netware account
               while(loRs.next()){
                  //is netware present
                  //System.out.println(loRs.getString("sUserIDxx") + "»" + this.Decrypt(loRs.getString("sLogNamex")));
                  if(loRs.getString("sUserIDxx")
                     .equalsIgnoreCase(lsNetWareX)){
                      //is netwares logname equal sysadmin
                     if(this.Decrypt(loRs.getString("sLogNamex"))
                        .equalsIgnoreCase(lsSysAdmin)){
                        hasNetWare = true;
                     }
                  }
               }
            }
         }
         catch(SQLException e){
            setMessage(e.getMessage());
         }
         finally{
            MiscUtil.close(loRs);
            MiscUtil.close(loPstmt);
         }
      }
      return hasNetWare;
   }

   /*
    * Load the configuration properties in the ini file
    */
//   private boolean loadIni(String fsProductID){
//
//      //Get configuration values
//       try{
//         IniFile ini = new IniFile();
//         psDBNameXX = ini.getValue(fsProductID, "Database");
//         psDBSrvrNm = ini.getValue(fsProductID, "ServerName");
//         psDBSrvrMn = ini.getValue(fsProductID, "MainServer");
//
//         psDBUserNm = this.Decrypt(ini.getValue(fsProductID, "UserName"));
//         psDBPassWD = this.Decrypt(ini.getValue(fsProductID, "Password"));
//
//         psDBPortNo = ini.getValue(fsProductID, "Port");
//         psClientID = ini.getValue(fsProductID, "ClientID");
//      }catch(Exception ex){
//         ex.printStackTrace();
//         setErrMsg(ex.getMessage());
//         return false;
//      }
//
//      //Test validity of Results
//      if(psDBNameXX.equals("") ||
//         psDBSrvrNm.equals("") ||
//         psDBUserNm.equals("") ||
//         psDBPassWD.equals("") ||
//         psClientID.equals("")) {
//          setErrMsg("Invalid configuration values!");
//          return false;
//      }
//
//      if (psDBPortNo.equals("")){
//         psDBPortNo = "3306";
//      }
//
//      return true;
//   }

   /*
    * Load the configuration properties in the ini file
    */
   private boolean loadConfig(String fsProductID){
      //System.out.println("loadConfig(String fsProductID)");
      //Get configuration values
      try{
         GProperty loProp = new GProperty("GhostRiderXP");
         
         if(loProp.getConfig(fsProductID + "-CryptType") != null){
            pnHexCrypt = Integer.valueOf(loProp.getConfig(fsProductID + "-CryptType"));
         }
         else{
            pnHexCrypt = 0;
         }
         
         psDBNameXX = loProp.getConfig(fsProductID + "-Database");
         System.out.println(psDBNameXX);
         psDBSrvrNm = loProp.getConfig(fsProductID + "-ServerName");
         System.out.println(psDBSrvrNm);
         psDBSrvrMn = loProp.getConfig(fsProductID + "-MainServer");
         System.out.println(psDBSrvrMn);
            
         //if(loProp.getConfig(fsProductID + "-UserName") != null){
         if(!loProp.getConfig(fsProductID + "-UserName").isEmpty()){
            psDBUserNm = this.Decrypt(loProp.getConfig(fsProductID + "-UserName"));
            psDBPassWD = this.Decrypt(loProp.getConfig(fsProductID + "-Password"));
         }
         else{
            psDBUserNm = "";
            psDBPassWD = "";
         }

         psDBPortNo = loProp.getConfig(fsProductID + "-Port");
         psClientID = loProp.getConfig(fsProductID + "-ClientID");
         System.out.println(psClientID);

      }catch(NumberFormatException ex){
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
         return false;
      }
        
      //Test validity of Results
      if(psDBNameXX.equals("") ||
         psDBSrvrNm.equals("") ||
         psClientID.equals("")) {
         setErrMsg("Invalid configuration values!");
         return false;
      }

      if (psDBPortNo == null || psDBPortNo.equals("")){
         psDBPortNo = "3306";
      }

      return true;
   }

   /**
    * Gets the java.sql.Connection instance currently use by the GRider instance.
    * <p>
    * If current Connection instance is null a reconnect is perform by this method.
    * 
    * @return  the java.sql.Connection instance.
    */
   public Connection getConnection(){
      if(poCon == null){
         System.out.println("Reset Connection");
         poCon = doConnect();
      }

      return poCon;
   }
   
   /**
    * gets the org.apache.commons.dbcp.BasicDataSource instance currently use by 
    * the GRider instance.
    * 
    * @return     the org.apache.commons.dbcp.BasicDataSource instance.
    */
   public DataSource getDataSource(){
      return poDS;
   }
    
   /**
    * Initiates a new transaction.
    * 
    * @return     true if successful, otherwise false.
    * 
    * @see #commitTrans()
    * @see #rollbackTrans()
    */
   public boolean beginTrans(){
      try {
         poCon.setAutoCommit(false);
         return true;
      } catch (SQLException ex) {
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
         return false;
      }
   }
   
   /**
    * Executes a SELECT SQL statement.
    * <p>
    * The method returns null if there are error(s) in the SELECT statement.
    * 
    * @param sql     the SELECT statement to execute.
    * @return        the ResultSet representation of the record.
    * 
    * @see executeUpdate
    * @see #executeQuery(String, String, String, String)
    */
    public ResultSet executeQuery(String sql){
        Statement loSQL = null;
        ResultSet oRS = null;
        try {
            loSQL = poCon.createStatement();
            oRS = loSQL.executeQuery(sql);
        } catch (SQLException ex) {
            setErrMsg(ex.getMessage());
            ex.printStackTrace();
            oRS = null;
        } finally{
            //MiscUtil.close(loSQL);
        }
        return oRS;
    }

   /**
    * Executes an SQL statement that manipulates the data or structure 
    * of the table or the database itself.
    * 
    * @param sql     the SQL statement to execute.
    * @return        the number of rows affected by the statement
    * 
    * @see #executeQuery(String sql)
    * @see #executeQuery(String sql, String table, String branch, String destinat)
    */
   public long executeUpdate(String sql){
      Statement loSQL = null;
      long lnRecord;
      try {
         loSQL = poCon.createStatement();
         lnRecord = loSQL.executeUpdate(sql);
      } catch (SQLException ex) {
         setErrMsg(ex.getMessage());
         lnRecord = 0;
      }finally{
         MiscUtil.close(loSQL);
      }
      return lnRecord;
   }
    
   /**
    * Executes an SQL statement that manipulates the data or structure 
    * of the table or the database itself.
    * <p>
    * Compared with executeUpdate(String sql), The SQL statement for 
    * this method is logged as part of the audit trail.
    * The logged statement is also use in the Replication System(Astro).
    * 
    * @param sql        the SQL statement to execute.
    * @param table      the name of the table to be manipulated.
    * @param branch     the branch code of the branch that owns the SQL statement.
    * @param destinat   the branch code of the recipient branch. 
    * @return           the number of rows affected by the statement
    * @see #executeUpdate(String sql)
    * @see #executeQuery(String sql)
    */
   public int executeQuery(String sql, String table, String branch, String destinat){
      boolean lbSuccess = false;
      Statement loSQL = null;
      Statement loLog = null;
      int lnRecord = 0;

      if(branch.isEmpty())
         branch = psBranchCd;
      
      //Determine what branch code will be use
      //Use the branch code of the main office if online
      //Use the branch code of the current branch if not
      String lsBranchCD = pbIsOnline ? "M001" : psBranchCd;
      
      try {
         //Execute the sql statement
         loSQL = poCon.createStatement();
         System.out.println(sql);
         lnRecord = loSQL.executeUpdate(sql);
         
         Timestamp tme = getServerDate();

         StringBuilder lsSQL = new StringBuilder();
         StringBuilder lsNme = new StringBuilder();

         //set fieldnames
         lsNme.append("(sTransNox");
         lsNme.append(", sBranchCd");
         lsNme.append(", sStatemnt");
         lsNme.append(", sTableNme");
         lsNme.append(", sDestinat");
         lsNme.append(", sModified");
         lsNme.append(", dEntryDte");
         lsNme.append(", dModified)");
         
         //set values
         lsSQL.append("(" + SQLUtil.toSQL(MiscUtil.getNextCode("xxxReplicationLog", "sTransNox", true, poCon, lsBranchCD)));
         lsSQL.append(", " + SQLUtil.toSQL(branch));
         lsSQL.append(", " + SQLUtil.toSQL(sql));
         lsSQL.append(", " + SQLUtil.toSQL(table));
         lsSQL.append(", " + SQLUtil.toSQL(destinat));
         lsSQL.append(", " + SQLUtil.toSQL((psUserIDxx == null ? "" : psUserIDxx)));
         lsSQL.append(", " + SQLUtil.toSQL(tme));
         lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");
         
         
//         //GCrypt loCrypt = new GCrypt();
//         lsSQL.append("INSERT INTO xxxReplicationLog SET");
//         lsSQL.append("  sTransNox = " + SQLUtil.toSQL(MiscUtil.getNextCode("xxxReplicationLog", "sTransNox", true, poCon, lsBranchCD)));
//         lsSQL.append(", sBranchCd = " + SQLUtil.toSQL(branch));
//         lsSQL.append(", sStatemnt = " + SQLUtil.toSQL(sql));
//         lsSQL.append(", sTableNme = " + SQLUtil.toSQL(table));
//         lsSQL.append(", sDestinat = " + SQLUtil.toSQL(destinat));
//         lsSQL.append(", sModified = " + SQLUtil.toSQL((psUserIDxx == null ? "" : psUserIDxx)));
//         lsSQL.append(", dEntryDte = " + SQLUtil.toSQL(tme));
//         lsSQL.append(", dModified = " + SQLUtil.toSQL(tme));
//         loLog = poCon.createStatement();
//         loLog.executeUpdate(lsSQL.toString());

         loLog = poCon.createStatement();
         loLog.executeUpdate("INSERT INTO xxxReplicationLog" + lsNme.toString() + " VALUES" + lsSQL.toString());
         
         tme = null;
         lsSQL = null;
         
      } catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         setErrMsg(ex.getMessage());
         lnRecord = 0;
      }
      finally{
         MiscUtil.close(loLog);
         MiscUtil.close(loSQL);
         
         loLog = null;
         loSQL = null;
         
      }
      return lnRecord;
   }

   /**
    * Commits the current transaction.
    * 
    * @return  true if successful in performing the commit, otherwise false
    * 
    * @see #beginTrans()
    * @see #rollbackTrans()
    */
   public boolean commitTrans(){
      boolean lbSuccess;
      try {
         poCon.commit();
         poCon.setAutoCommit(true);
         lbSuccess = true;
      } catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         setErrMsg(ex.getMessage());
         lbSuccess = false;
      }
      
      return lbSuccess;
   }

   /**
    * Rollback the current transaction.
    * 
    * @return  true if successful in performing the rollback, otherwise false.
    * @see #commitTrans()
    * @see #beginTrans()
    */
   public boolean rollbackTrans(){
      boolean lbSuccess;
      try {
         poCon.rollback();
         poCon.setAutoCommit(true);
			lbSuccess = true;
      } catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         setErrMsg(ex.getMessage());
         lbSuccess = false;
      }
      return lbSuccess;
   }

   /**
    * Indicates whether update will be save in the local server or in the main server.
    * <p>
    * Triggers re-establishment of connection to the database server.
    * <p>
    * Default server is the local server of the client.
    * 
    * @param bOnline    true if main server, otherwise local server.
    * 
    * @return           true if attempt to change server is successful, otherwise false.
    */
   public boolean setOnline(boolean bOnline){
      //kalyptus - 2017.09.06 02:12pm
      //If SQLite, simply return true;
      try{
         if(poCon.getMetaData().getDriverName().equalsIgnoreCase("SQLite JDBC")){
            return true;
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         return false;
      } 
       
      if(pbIsOnline == bOnline)
         return true;

      pbIsOnline = bOnline;

      poCon = doConnect();

      if(poCon == null){
         pbIsOnline = false;
         System.out.println("Unable to connect");
         return false;
      }
      else{
         System.out.println("Connected successfully");
         return true;
      }
   }    

   public boolean isOnline(){
      return pbIsOnline;
   }
    
   /*
    * getters for application environment here
    */
   public String getClientID(){
      return psClientID;
   }
   public String getClientName(){
      return psClientNm;
   }
   public String getAddress(){
      return psAddressx;
   }
   public String getTownName(){
      return psTownName;
   }
   public String getZipCode(){
      return psZippCode;
   }
   public String getProvince(){
      return psProvName;
   }
   public String getTelNo(){
      return psTelNoxxx;
   }
   public String getFaxNo(){
      return psFaxNoxxx;
   }
   public String getProductID(){
      return psProdctID;
   }
   public String getProductName(){
      return psProdctNm;
   }
   public String getSysAdmin(){
      return psSysAdmin;
   }
   public String getNetWare(){
      return psNetWarex;
   }
   public String getMachine(){
      return psMachinex;
   }
   public String getApproval(){
      return psApproved;
   }
   public String getApplPath(){
      return psApplPath;
   }
   public String getReportPath(){
      return psReptPath;
   }
   public String getImagePath(){
      return psImgePath;
   }
   public Date getSysDate(){
      return pdSysDatex;
   }
   public int getNetError(){
      return pnNetError;
   }
   public String getBranchCode(){
      return psBranchCd;
   }
   public Date getLicenceDate(){
      return pdLicencex;
   }

    /*
     *  Other Branch Info here
     */
   public String getBranchName(){
      return psBranchNm;
   }
   public boolean isWarehouse(){
      return (pcWareHous.equals("1") ? true : false);
   }
   public boolean isMainOffice(){
      return (pcMainOffc.equals("1") ? true : false);
   }

    /*
     *  User Info here
     */

   public String getUserID(){
      return psUserIDxx;
   }
   public int getUserLevel(){
      return pnUserLevl;
   }
   public String getEmployeeNo(){
      return psEmployNo;
   }

/*
 *  Connection info
 */
//    public String getDBServer(){
//        return psDBSrvrNm;
//    }
//    public String getDBName(){
//        return psDBNameXX;
//    }
//    public String getDBUser(){
//        return psDBUserNm;
//    }
//    public String getDBPassword(){
//        return psDBPassWD;
//    }
//    public String getDBPort(){
//        return psDBPortNo;
//    }

   private String getSQ_LoadConfig(){
       return  "SELECT" +
                     "  a.sClientID" +
                     ", a.sClientNm" +
                     ", a.sAddressx" +
                     ", a.sTownName" +
                     ", a.sZippCode" +
                     ", a.sProvName" +
                     ", a.sTelNoxxx" +
                     ", a.sFaxNoxxx" +
                     ", a.sApproved" +
                     ", a.sBranchCd" +
                     ", b.sProdctID" +
                     ", b.sProdctNm" +
                     ", b.sApplName" +
                     ", c.sApplPath" +
                     ", c.sReptPath" +
                     ", c.sImgePath" +
                     ", c.sSysAdmin" +
                     ", c.sNetWarex" +
                     ", c.sMachinex" +
                     ", c.dSysDatex" +
                     ", c.dLicencex" +
                     ", c.nNetError" +
                     ", c.sSkinCode" +
                     ", d.sComptrNm" +
                     ", e.sBranchNm" +
                     ", e.cWareHous" +
                     ", e.cMainOffc" +
                     ", f.sDBHostNm" +
              " FROM xxxSysClient a" +
                  ", xxxSysObject b" +
                  ", xxxSysApplication c" +
                       " LEFT JOIN xxxSysWorkStation d" +
                          " ON c.sClientID = d.sClientID" +
                             " AND d.sComptrNm = ?"  +
                 ", Branch e" +
                 ", Branch_Others f" + 
              " WHERE c.sClientID = a.sClientID" +
                 " AND c.sProdctID = b.sProdctID" +
                 " AND a.sBranchCd = e.sBranchCd" +
                 " AND a.sBranchCD = f.sBranchCD" +  
                 " AND a.sClientID = ?" +
                 " AND b.sProdctID = ?";
   }

   private String getSQ_Signature(){
      return  "SELECT" +
                     "  sUserIDxx" +
                     ", sUserName" +
                     ", sLogNamex" +
             " FROM xxxSysUser" +
             " WHERE sUserIDxx IN (?, ?)";
   }

   /*
    * setters and getters for the error message
    */
   public String getErrMsg(){
      return psErrorMsg;
   }
   public void setErrMsg(String fsMessage){
      psErrorMsg = fsMessage;
   }
   public String getMessage(){
      return psMessages;
   }
   public void setMessage(String fsMessage){
      psMessages = fsMessage;
   }

   /**
    * Create a BasicDataSource instance for MySQL driver
    * 
    * @param fsURL      the IP/Hostname of the MySQL server.
    * @param fsDBF      the name of the database to be used in the connection
    * @param fsUser     the MySQL user's name
    * @param fsPassWD   the MySQL user's password 
    * @param fsPort     the MySQL server's listening port.
    * @return           the BasicDataSource instance.
    */
   public DataSource setupDataSource(String fsURL, String fsDBF, String fsUser, String fsPassWD, String fsPort ) {
      BasicDataSource ds = new BasicDataSource();
      ds.setDriverClassName("com.mysql.jdbc.Driver");
      ds.setUsername(fsUser);
      ds.setPassword(fsPassWD);
      ds.setUrl("jdbc:mysql://" + fsURL + ":" + fsPort + "/" + fsDBF );  //+ "?useUnicode=true&characterEncoding=UTF-8"
      return ds;
    }
   
   /**
    * Create a BasicDataSource instance for SQLite driver
    * 
    * @param fsURL      the location of the SQLite file
    * @param fsDBF      the name of the SQLite file
    * @return           the BasicDataSource instance.
    */ 
   public DataSource setupDataSource(String fsURL, String fsDBF) {
      BasicDataSource ds = new BasicDataSource();
      ds.setDriverClassName("org.sqlite.JDBC");
      ds.setUrl("jdbc:sqlite:" + fsURL + fsDBF);
      return ds;
   }   
   
   /**
    * Gets the name of the client computer.
    * <p>
    * The information are extracted from the database server if connected to MySQL.
    * Otherwise, the method uses InetAddress object in extracting the information.
    * 
    * @return     the computer/host name
    */
   public String HostName(){
      String hostname = "";
      if(poCon == null)  
          return "";
        
      try {
         if(poCon.getMetaData().getDriverName().equalsIgnoreCase("SQLite JDBC")){
            try{
               InetAddress addr;
               addr = InetAddress.getLocalHost();
               hostname = addr.getHostName();           
            }
            catch (UnknownHostException ex){
               Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
               setErrMsg(ex.getMessage());
            }
         }
         else{
            ResultSet loRS = null;
            String lsSQL = "SHOW VARIABLES LIKE 'hostname'";
            loRS = poCon.createStatement().executeQuery(lsSQL);

            if(loRS.next()) 
                hostname = loRS.getString("Value");

            MiscUtil.close(loRS);
            loRS = null;
         }
      } catch (SQLException ex) {
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         setErrMsg(ex.getMessage());
      }
        
      return hostname;
   }
   
   /**
    * Gets the additional information/configuration of the branch.
    * <p>
    * The additional Information/configuration of the branch are stored in Branch_Others.
    * The method returns an empty string if the field indicated is not in Branch_Others.
    * 
    * @param branch  the branch code of the branch whose other info will be retrieve.
    * @param field   the field name of the information to extract.
    * @return        the information stored in the field.
    */
   public String Config(String branch, String field){
      String config = "";
      if(poCon == null)  
         return config;

      ResultSet loRS = null;
      String lsSQL = "SELECT *" + 
                    " FROM Branch_Others" + 
                    " WHERE sBranchCD = '" + branch + "'";

      try {
         loRS = poCon.createStatement().executeQuery(lsSQL);

         if(loRS.next()) 
            config = loRS.getString(field);
      } catch (SQLException ex) {
         Logger.getLogger(GRider.class.getName()).log(Level.SEVERE, null, ex);
         setErrMsg(ex.getMessage());
      }
      finally{
         MiscUtil.close(loRS);
      }
        
      return config;
   }
   
   /**
    * Encrypts a string value.
    * <p>
    * Note: The returned encrypted value is a hexadecimal string.
    * 
    * @param value   the value to encrypt.
    * @param salt    the salt value to be used during decryption.
    * @return        the encrypted value of the string value.
    * @see Decrypt
    */
   public String Encrypt(String value, String salt){
      if(value == null || value.trim().length() == 0 || salt == null || salt.trim().length() == 0)
         return null;
    
      try {
         GCrypt loCrypt = new GCrypt(salt.getBytes("ISO-8859-1"));
         byte[] ret = loCrypt.encrypt(value.getBytes("ISO-8859-1"));
         
         if(pnHexCrypt == 1){
            return Hex.encodeHexString(ret);
         }
         else{
            return new String(ret, "ISO-8859-1");
         }
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * Encrypts a string value.
    * <p>
    * Note: The returned encrypted value is a hexadecimal string.
    * 
    * @param value   the value to encrypt.
    * @return        the encrypted value of the value.
    * @see Decrypt
    */
   public String Encrypt(String value) {
      return Encrypt(value, SIGNATURE);
   }
   
   /**
    * Decrypts the encrypted value.
    * <p>
    * Note: The encrypted value is a hexadecimal string.
    * 
    * @param value   the value to decrypt.
    * @param salt    the salt value to be used during decryption.
    * @return        the decrypted value of value.
    * @see Encrypt
    */
   public String Decrypt(String value, String salt) {
      if(value == null || value.trim().length() == 0 || salt == null || salt.trim().length() == 0)
               return null;

      byte[] hex;
      try {
         if(pnHexCrypt == 1){
            try {
               hex = Hex.decodeHex(value);
            } catch (DecoderException e1) {
               return null;
            }
         }
         else{
            hex = value.getBytes("ISO-8859-1");
         }
         //System.out.println(new String(hex, "ISO-8859-1"));
         //System.out.println(value);
         //remove this part if returning the new logic...
         GCrypt loCrypt = new GCrypt(salt.getBytes("ISO-8859-1"));
         byte ret[] = loCrypt.decrypt(hex);

         return new String(ret, "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    * Decrypts the encrypted value.
    * <p>
    * Note: The encrypted value is a hexadecimal string.
    * 
    * @param value   the envalue to decrypt
    * @return        the decrypted value of value.
    * @see Encrypt
    */
   public String Decrypt(String value){
      return Decrypt(value, SIGNATURE);
   }

   /**
    * Gets the name of the validator for specified transaction and condition. 
    * <p>
    * Validator(s) are stored in the xxxSysValidator.
    * 
    * @param parent  the name of the 'interface' for the validator.
    * @param source  the source/transaction type that will enable us to distinquish the validator from its siblings.
    * @return        the complete name of the validator.
    * @see MiscUtil#createInstance
    */
   public String getValidator(String parent, String source){
      String ret; 
      try {
         String lsSQL = "SELECT sClassNme" +
                  " FROM xxxSysValidator" +
                  " WHERE sInterfce = " + SQLUtil.toSQL(parent) +
                  " AND sClassCde = " + SQLUtil.toSQL(source);
         ResultSet loRS = this.executeQuery(lsSQL);
          
         if(loRS == null){
            ret = null;
         }
         else if(!loRS.next()){
            ret = null;
         }
         else{
            ret = loRS.getString("sClassNme");
         }
          
      } catch (SQLException ex) {
         ex.printStackTrace();
         ret = null;
      }
      
      return ret;
   }
   
   public GDBFChain getGDBFChain(String host){
       GDBFChain chain = new GDBFChain();
       
       if(pbIsOnline && host.equalsIgnoreCase(psDBSrvrMn)){
           chain.doDBFChain(psBranchCd, psUserIDxx, poCon);
       }
       else if(!pbIsOnline && host.equalsIgnoreCase(psDBSrvrNm)){
           chain.doDBFChain(psBranchCd, psUserIDxx, poCon);
       }
       else{
           chain.doDBFChain(psBranchCd, psUserIDxx, host, psDBNameXX, psDBUserNm, psDBPassWD, psDBPortNo);
       }
       
       return chain;
   }
}


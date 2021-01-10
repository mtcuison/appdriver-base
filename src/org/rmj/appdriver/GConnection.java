/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * =========================================
 * Sample Usage: Initialization
 *
 * GConnection loGCon = new GConnection();
 * loGCon.setBranch("01");
 * loGCon.setUser("0109035");
 * loGCon.setupDataSource("localhost", "GMC_ISysDBF", "sa", "Wtrtwh", "3306");
 *      :
 *      :
 * TimeStamp loTime = loGCon.getServerDate();
 *      :
 *      :
 * loGCon.beginTrans();
 * int lnRecdCtrx = loGCon.executeQuery(...);
 * if(lnRecdCtrxx > 0)
 *     loGCon.commitTrans();
 * else
 *     loGCon.rollbackTrans();
 * =========================================
 */

package org.rmj.appdriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * 
 * @author kalyptus
 */
public class GConnection{
   public void setupDataSource(String fsURL, String fsDBF, String fsUser, String fsPassWD, String fsPort ) {
      BasicDataSource ds = new BasicDataSource();
      ds.setDriverClassName("com.mysql.jdbc.Driver");
      ds.setUsername(fsUser);
      ds.setPassword(fsPassWD);
      ds.setUrl("jdbc:mysql://" + fsURL + ":" + fsPort + "/" + fsDBF);
      poDS = ds;
   }

   public void setupDataSource(String fsURL, String fsDBF) {
      BasicDataSource ds = new BasicDataSource();
      ds.setDriverClassName("org.sqlite.JDBC");
      ds.setUrl("jdbc:sqlite:" + fsURL + fsDBF);
      poDS = ds;
   }
   
   public Connection getConnection(){
      return poCon;
   }

   /*
    * Connect to the database using the values in the Data Source
    */
   public Connection doConnect(){
      if(poDS == null)
          return null;

      try{
         if(poCon != null)
            poCon.close();

         poCon = poDS.getConnection();
         return poCon;
      }
      catch(SQLException ex){
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
      catch(Exception ex){
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }

      return null;
   }


    public void beginTrans(){
      try {
         poCon.setAutoCommit(false);
      }
      catch (SQLException ex) {
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
   }

   //Purpose: executes an SQL statement without the prenumbered transaction number.
   //         transaction number are auto-generated during the execution.
   public int executeQuery(String sql, String table, String branch, String destinat){
      String lsLogNo = MiscUtil.getNextCode("xxxReplicationLog", "sTransNox", true, poCon, psBranchCD);
      return executeQuery(lsLogNo, sql, table, branch, destinat);
   }

    //Purpose: executes an SQL statement with prenumbered transaction number.
    //         Could be use to solve the problem in [Posting of GCard Detail Offline Transaction].
    //         Log will be posted at the main server and at the localhost.
    //         transnox are generated from the localhost.
   public int executeQuery(String transno, String sql, String table, String branch, String destinat){
      Statement loSQL = null;
      Statement loLog = null;
      int lnRecord = 0;
      try {
         //Execute the sql statement
         loSQL = poCon.createStatement();
         lnRecord = loSQL.executeUpdate(sql);

         Timestamp tme = getServerDate();

//         GCrypt loCrypt = new GCrypt();
//         lsSQL.append("INSERT INTO xxxReplicationLog SET");
//         lsSQL.append("  sTransNox = " + SQLUtil.toSQL(transno));
//         lsSQL.append(", sBranchCd = " + SQLUtil.toSQL(branch));
//         lsSQL.append(", sStatemnt = " + SQLUtil.toSQL(sql));
//         lsSQL.append(", sTableNme = " + SQLUtil.toSQL(table));
//         lsSQL.append(", sDestinat = " + SQLUtil.toSQL(destinat));
//         lsSQL.append(", sModified = " + SQLUtil.toSQL(loCrypt.encrypt(psUserIDxx)));
//         lsSQL.append(", dEntryDte = " + SQLUtil.toSQL(tme));
//         lsSQL.append(", dModified = " + SQLUtil.toSQL(tme));
//
//         loLog = poCon.createStatement();
//         loLog.executeUpdate(lsSQL.toString());

         StringBuilder lsSQL = new StringBuilder();
         StringBuilder lsNme = new StringBuilder();

         //set fieldnames
         lsSQL.append("(sTransNox");
         lsSQL.append(", sBranchCd");
         lsSQL.append(", sStatemnt");
         lsSQL.append(", sTableNme");
         lsSQL.append(", sDestinat");
         lsSQL.append(", sModified");
         lsSQL.append(", dEntryDte");
         lsSQL.append(", dModified)");

         //set values
         lsSQL.append("(" + SQLUtil.toSQL(transno));
         lsSQL.append(", " + SQLUtil.toSQL(branch));
         lsSQL.append(", " + SQLUtil.toSQL(sql));
         lsSQL.append(", " + SQLUtil.toSQL(table));
         lsSQL.append(", " + SQLUtil.toSQL(destinat));
         lsSQL.append(", " + SQLUtil.toSQL((psUserIDxx == null ? "" : psUserIDxx)));
         lsSQL.append(", " + SQLUtil.toSQL(tme));
         lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");        

         loLog = poCon.createStatement();
         loLog.executeUpdate("INSERT INTO xxxReplicationLog" + lsNme.toString() + " VALUES" + lsSQL.toString());

         psLastLogx = transno;

      } catch (SQLException ex) {
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
         lnRecord = 0;
         psLastLogx = "";
      }
      finally{
         MiscUtil.close(loLog);
         MiscUtil.close(loSQL);
      }
      return lnRecord;
   }

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
   
   public void commitTrans(){
      try {
         poCon.commit();
         poCon.setAutoCommit(true);
      } catch (SQLException ex) {
         Logger.getLogger(GConnection.class.getName()).log(Level.SEVERE, null, ex);
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
   }

   public void rollbackTrans(){
      try {
         poCon.rollback();
         poCon.setAutoCommit(true);
      } catch (SQLException ex) {
         Logger.getLogger(GConnection.class.getName()).log(Level.SEVERE, null, ex);
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
   }

   /*
    * get the timestamp from the mysql server
    */
   public Timestamp getServerDate(){
      Connection loCon = null;
      ResultSet loRS = null;
      Timestamp loTimeStamp = null;
        

      if(poDS == null){
         setMessage("Invalid Data Source");
         return null;
      }

      try{
         if(poCon == null)
            loCon = doConnect();
         else
            loCon = poCon;

         System.out.println(loCon.getMetaData().getDriverName());
         
         String lsSQL="";
         if(loCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
            lsSQL = "SELECT DATETIME('now','localtime')";
         }else{
            //assume that default database is MySQL ODBC
            lsSQL = "SELECT SYSDATE()";
         }
         
         loRS = loCon.createStatement()
                     .executeQuery(lsSQL);
         //position record pointer to the first record
         loRS.next();
         //assigned timestamp
         //loTimeStamp = loRS.getTimestamp(1);
         loTimeStamp = Timestamp.valueOf(loRS.getString(1));  
               
      }
      catch(SQLException ex){
         Logger.getLogger(GConnection.class.getName()).log(Level.SEVERE, null, ex);
         ex.printStackTrace();
         setErrMsg(ex.getMessage());
      }
      finally{
         MiscUtil.close(loRS);
         if (poCon == null) MiscUtil.close(loCon);
      }
      return loTimeStamp;
   }

   public void setBranch(String branch){
      psBranchCD = branch;
   }

   public void setUser(String user){
      psUserIDxx = user;
   }

   public String getLastLog(){
      return psLastLogx;
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

   private BasicDataSource poDS;
   private Connection poCon;
   private String psUserIDxx;
   private String psBranchCD;

   private String psLastLogx;

   //Error Catcher
   private String psErrorMsg = "";
   private String psMessages = "";
}



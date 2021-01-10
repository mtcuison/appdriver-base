/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmj.appdriver;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author user
 */
public class GDBFChain {
    private String psMessages = "";
    private Connection poCon;
    private String psUserIDxx;
    private String psBranchCD;
    private String psSignature;
    private String psLastLogNo;
    private String psLastQryNo;
    private String psLstAudtNo;
    
    public static int pxeLastLog = 0;
    public static int pxeLastQuery=1;
    public static int pxeLastAudit=2;
    
    public GDBFChain(){
        psMessages = "";
        psLastLogNo = "";
        psLastQryNo = "";
        psLstAudtNo = "";
    }
    
    public String getMessage(){
        return psMessages;
    }

    public String getLastQueryNo(){
        return psLastQryNo;
    }
    
    public String getLastReplNo(){
        return psLastLogNo;
    }
    
    public String getLastAuditNo(){
        return psLstAudtNo;
    }
    
    public Connection doConnect(String fsURL, String fsDBF, String fsUser, String fsPassWD, String fsPort ) {
        Connection loCon = null;
      
        System.out.println("doConnect()");
      
        try {
            System.out.println("Connecting to " + fsURL);
            loCon = MiscUtil.getConnection(fsURL, fsDBF, fsUser, fsPassWD, fsPort);
        } catch (SQLException ex) {
            psMessages = ex.getMessage();
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            psMessages = ex.getMessage();
            ex.printStackTrace();
        }
        return loCon;
    }
    
    public void doDBFChain(String branch, String user, String fsURL, String fsDBF, String fsUser, String fsPassWD, String fsPort){
        psBranchCD = branch;
        psUserIDxx = user;
        poCon = doConnect(fsURL, fsDBF, fsUser, fsPassWD, fsPort);
    }
    
    public void doDBFChain(String branch, String user, Connection con){
        psBranchCD = branch;
        psUserIDxx = user;
        poCon = con;
    }

    public Connection getConnection(){
        return poCon;
    }
    
    public boolean beginTrans(){
        if(poCon == null){
            psMessages = "beginTrans: Invalid connection...";
            return false;
        }
        
        try {
            poCon.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            psMessages = ex.getMessage();
            return false;
       }
    }
    
   public boolean rollbackTrans(){
        if(poCon == null){
            psMessages = "beginTrans: Invalid connection...";
            return false;
        }

        try {
            poCon.rollback();
            poCon.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            psMessages = ex.getMessage();
            return false;
        }
   }

    public boolean commitTrans(){
        if(poCon == null){
            psMessages = "beginTrans: Invalid connection...";
            return false;
        }

        try {
            poCon.commit();
            poCon.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            psMessages = ex.getMessage();
            return false;
        }
    }

    public ResultSet executeQuery(String sql){
        if(poCon == null){
            psMessages = "beginTrans: Invalid connection...";
            return null;
        }

        Statement loSQL = null;
        ResultSet oRS = null;
        try {
            loSQL = poCon.createStatement();
            oRS = loSQL.executeQuery(sql);
        } catch (SQLException ex) {
            psMessages = ex.getMessage();
            ex.printStackTrace();
            oRS = null;
        }
        return oRS;
    }
    
    public long executeUpdate(String sql){
        if(poCon == null){
            psMessages = "beginTrans: Invalid connection...";
            return 0;
        }

        Statement loSQL = null;
        long lnRecord;
        try {
            loSQL = poCon.createStatement();
            lnRecord = loSQL.executeUpdate(sql);
        } catch (SQLException ex) {
            psMessages = ex.getMessage();
            lnRecord = 0;
        }finally{
            MiscUtil.close(loSQL);
        }
        return lnRecord;
    }

    public boolean logQuery(String sql, String table, String branch, String destinat, String user){
       String branchcd; 
       
       branch = branch.trim();
       int divx = NumberUtils.isNumber(branch) ? Integer.valueOf(branch) : 0;       
       if(divx > 0 || branch.isEmpty()){
            branchcd = psBranchCD;
       }
       else{
          branchcd = branch;
       }
        
        Timestamp tme = getServerDate();
        
        if(tme == null){
            return false;
        }
        
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        psLastLogNo = MiscUtil.getNextCode("xxxReplicationLog", "sTransNox", true, poCon, branchcd);
        //set fieldnames
        lsNme.append("(sTransNox");
        lsNme.append(", sBranchCd");
        lsNme.append(", sStatemnt");
        lsNme.append(", sTableNme");
        lsNme.append(", sDestinat");
        lsNme.append(", sModified");
        lsNme.append(", dEntryDte");
        lsNme.append(", dModified)");

        //if replication to the division then set branchcd to the division 
        //specified...
        if(divx > 0){
           branchcd = branch;
        }
        
        lsSQL.append("(" + SQLUtil.toSQL(psLastLogNo));
        lsSQL.append(", " + SQLUtil.toSQL(branchcd));
        lsSQL.append(", " + SQLUtil.toSQL(sql));
        lsSQL.append(", " + SQLUtil.toSQL(table));
        lsSQL.append(", " + SQLUtil.toSQL(destinat));
        lsSQL.append(", " + SQLUtil.toSQL((user == null ? "" : user)));
        lsSQL.append(", " + SQLUtil.toSQL(tme));
        lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");

        long count = executeUpdate("INSERT INTO xxxReplicationLog" + lsNme.toString() + " VALUES" + lsSQL.toString());
        
        return(count > 0);
    }
    
    public boolean logQuery(String sql, String branch, String user){
        if(branch.isEmpty()){
            branch = psBranchCD;
        }

        Timestamp tme = getServerDate();
        
        if(tme == null){
            return false;
        }
        
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        psLastQryNo = MiscUtil.getNextCode("xxxQueryLog", "sTransNox", true, poCon, branch);
        //set fieldnames
        lsNme.append("(sTransNox");
        lsNme.append(", sBranchCd");
        lsNme.append(", sStatemnt");
        lsNme.append(", sModified");
        lsNme.append(", dModified)");

        //set values
        lsSQL.append("(" + SQLUtil.toSQL(psLastQryNo));
        lsSQL.append(", " + SQLUtil.toSQL(branch));
        lsSQL.append(", " + SQLUtil.toSQL(sql));
        lsSQL.append(", " + SQLUtil.toSQL((user == null ? "" : user)));
        lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");

        long count = executeUpdate("INSERT INTO xxxQueryLog" + lsNme.toString() + " VALUES" + lsSQL.toString());
        
        return(count > 0);
    }

    public boolean logAudit(String object, String referno, String eventid, String remarks, String serialno, String computer, String user){

        Timestamp tme = getServerDate();
        
        if(tme == null){
            return false;
        }
        
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        psLstAudtNo = MiscUtil.getNextCode("xxxAuditTrail", "sTransNox", true, poCon, psBranchCD);
        //set fieldnames
        lsNme.append("(sTransNox");
        lsNme.append(", sObjectCd");
        lsNme.append(", sReferNox");
        lsNme.append(", sEventIDx");
        lsNme.append(", sRemarksx");
        lsNme.append(", cStatusxx");
        lsNme.append(", cTranStat");
        lsNme.append(", sSerialNo");
        lsNme.append(", sComptrNm");
        lsNme.append(", sModified");
        lsNme.append(", dModified)");

        //set values
        lsSQL.append("(" + SQLUtil.toSQL(psLstAudtNo));
        lsSQL.append(", " + SQLUtil.toSQL(object));
        lsSQL.append(", " + SQLUtil.toSQL(referno));
        lsSQL.append(", " + SQLUtil.toSQL(eventid));
        lsSQL.append(", " + SQLUtil.toSQL(remarks));
        lsSQL.append(", " + SQLUtil.toSQL("0"));
        lsSQL.append(", " + SQLUtil.toSQL("0"));
        lsSQL.append(", " + SQLUtil.toSQL(serialno));
        lsSQL.append(", " + SQLUtil.toSQL(computer));
        lsSQL.append(", " + SQLUtil.toSQL((user == null ? "" : user)));
        lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");

        long count = executeUpdate("INSERT INTO xxxAuditTrail" + lsNme.toString() + " VALUES" + lsSQL.toString());
        
        return(count > 0);
    }
    
    
    private Timestamp getServerDate(){
        try {
            String lsSQL = "SELECT SYSDATE()";
            ResultSet loRS = executeQuery(lsSQL);
            //position record pointer to the first record
            loRS.next();
            //assigned timestamp
            Timestamp loTimeStamp = loRS.getTimestamp(1);
            
            return loTimeStamp;
        } catch (SQLException ex){
            psMessages = ex.getMessage();
            return null;
        }
    }
}

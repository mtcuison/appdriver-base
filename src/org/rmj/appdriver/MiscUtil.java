/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rmj.appdriver;

import org.rmj.appdriver.iface.GEntity;
import java.io.UnsupportedEncodingException;
//Used by getPCName
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains miscellaneous methods for JDBC objects.
 * 
 * @author kalyptus
 */
public class MiscUtil {
    /**
     * Gets the name of the computer.
     * 
     * @return the computer name.
     */
   public static String getPCName(){
      try{
         return InetAddress.getLocalHost().getHostName();
      }catch (Exception ex){
         ex.printStackTrace();
         return null;
      }
   }

   /**
    * Creates a Connection object for a MySQL server.
    * 
    * @param fsURL      the IP/Hostname of the MySQL server.
    * @param fsDatabase the name of the database to be used in the connection
    * @param fsUserID   the MySQL user's name
    * @param fsPassword the MySQL user's password 
    * @return           instance of the connection class.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword) throws SQLException, ClassNotFoundException{
      return getConnection(fsURL, fsDatabase, fsUserID, fsPassword, "3306");
   }

   /**
    * Connect to the MySQL Data using custom set port
    * 
    * @param fsURL      the IP/Hostname of the MySQL server.
    * @param fsDatabase the name of the database to be used in the connection
    * @param fsUserID   the MySQL user's name
    * @param fsPassword the MySQL user's password 
    * @param fsPort     the MySQL server's listening port.
    * @return           instance of the connection class.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword, String fsPort) throws SQLException, ClassNotFoundException{
      Connection oCon = null;
      Class.forName("com.mysql.jdbc.Driver");
      oCon = DriverManager.getConnection("jdbc:mysql://" + fsURL + ":" + fsPort + "/" + fsDatabase + "?useUnicode=true&characterEncoding=ISO-8859-1", fsUserID, fsPassword);
      return oCon;
   }
   
   /**
    * Connect to the SQLite database.
    * 
    * @param fsURL      the location of the SQLite file
    * @param fsDatabase the name of the SQLite file
    * @return           the BasicDataSource instance.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase) throws SQLException, ClassNotFoundException{
        //Connection oCon = null;
        //Class.forName("org.sqlite.JDBC");
        //oCon = DriverManager.getConnection("jdbc:sqlite:" + fsURL + fsDatabase);
        //return oCon;
        Connection conn;
        try {
            String url = "jdbc:sqlite:" + fsURL + fsDatabase;
            conn = DriverManager.getConnection(url);
            
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
   }    
    
    /**
     * Closes the ResultSet object.
     * 
     * @param foRs   The ResultSet to close.
     */
   public static void close(java.sql.ResultSet foRs) {
      if ( foRs == null) {
         return;
      }
      try {
         foRs.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
   }

    /**
     * Closes the Statement object.
     * 
     * @param foStmt The statement object to close.
     */
   public static void close(java.sql.Statement foStmt) {
      if (foStmt == null) {
         return;
      }
      try {
         foStmt.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
          //Ignore the error
      }
      finally{
         foStmt = null;
      }
   }

    /**
     * Closes the PreparedStatement object.
     * 
     * @param foPstmt The PreparedStatement to close. 
     */
   public static void close(java.sql.PreparedStatement foPstmt) {
      if (foPstmt == null) {
         return;
      }
      try {
         foPstmt.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
      finally{
         foPstmt = null;
      }
   }

   /**
    * Closes the Connection object.
    * 
    * @param foConn The connection object to close.
    */
   public static void close(java.sql.Connection foConn) {
      System.out.println("close:" + foConn.toString());
      if (foConn == null) {
         return;
      }
      try {
         foConn.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
      finally{
         foConn = null;
      }
   }
   /**
    * Count the number of rows within the ResultSet.
    * 
    * @param rs   The ResultSet to count.
    * @return     The number of rows within the ResultSet.
    */ 
   public static long RecordCount(java.sql.ResultSet rs) {
      long pos;
      long ctr;
      boolean frst = false;
      boolean last = false;
      try {
         frst = rs.isBeforeFirst();
         last = rs.isAfterLast();
         pos = rs.getRow();

         rs.beforeFirst();

         ctr = 0;
         while(rs.next())
            ctr++;

         if(pos > 0) {
            rs.absolute((int) pos);
         }
         else if(frst){
            rs.beforeFirst();
         }
         else{
            rs.afterLast();
         }
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         ctr=0;
      }
    	
      return ctr;
    }

   /**
    * Creates a SQL INSERT statement from a POJO instance.
    * 
    * @param foNewEntity   The POJO to convert into SQL INSERT statement.
    * @return              The SQL INSERT equivalent of GEntity.
    */ 
   public static String makeSQL(GEntity foNewEntity){
      StringBuilder lsSQL = new StringBuilder();
      StringBuilder lsNme = new StringBuilder();
      for(int lnCol = 1; !foNewEntity.getColumn(lnCol).equals("");lnCol++){
         lsSQL.append(", " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
         lsNme.append(", " + foNewEntity.getColumn(lnCol));
      }

      return "INSERT INTO " + foNewEntity.getTable() + "(" + lsNme.toString().substring(1) + ") VALUES (" +  
         lsSQL.toString().substring(1) + ")";
    }
    
   public static String makeSQL(GEntity foNewEntity, GEntity foPrevEntity, String fsCondition){
      StringBuilder lsSQL = new StringBuilder();
      int lnCol1 = 0;
      int lnCol2 = 0;

      for(int lnCol = 1; !foNewEntity.getColumn(lnCol).equals("");lnCol++){
         if(lnCol1 == 0 || lnCol2 == 0){
            if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("smodified"))
               lnCol1 = lnCol;
            else if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("dmodified"))
               lnCol2 = lnCol;
            else{
               if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                  lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));

//                  if(!(foNewEntity.getValue(lnCol) == null && foPrevEntity.getValue(lnCol) == null))
//                      if(!foNewEntity.getValue(lnCol).equals(foPrevEntity.getValue(lnCol)))
//                        lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
            }
         }
         else{
            if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
               lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));

//                if(!(foNewEntity.getValue(lnCol) == null && foPrevEntity.getValue(lnCol) != null))
//                  if(!foNewEntity.getValue(lnCol).equals(foPrevEntity.getValue(lnCol)))
//                     lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
         }
      }

      //If no update was detected return an empty string
      if(lsSQL.toString().equals(""))
         return "";

      //Add the value of smodified if the field is available
      if(lnCol1 > 0)
         lsSQL.append( ", sModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol1)));
      //Add the value of dmodified if the field is available
      if(lnCol2 > 0)
         lsSQL.append( ", dModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol2)));

      System.out.println("UPDATE " + foNewEntity.getTable() + " SET" +
                  lsSQL.toString().substring(1) +
            " WHERE " + fsCondition);

      return "UPDATE " + foNewEntity.getTable() + " SET" +
                  lsSQL.toString().substring(1) +
            " WHERE " + fsCondition;
   }    

   public static String addCondition(String SQL, String condition){
      int lnIndex;
      StringBuffer lsSQL = new StringBuffer(SQL);
      if(lsSQL.indexOf("WHERE") > 0){
         //inside
         if(lsSQL.indexOf("GROUP BY") > 0){
            lnIndex = lsSQL.indexOf("GROUP BY");
            lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }   
         else if(lsSQL.indexOf("HAVING") > 0){
           lnIndex = lsSQL.indexOf("HAVING");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else if(lsSQL.indexOf("ORDER BY") > 0){
           lnIndex = lsSQL.indexOf("ORDER BY");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else if(lsSQL.indexOf("LIMIT") > 0){
           lnIndex = lsSQL.indexOf("LIMIT");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else
            lsSQL.append(" AND (" + condition + ")");
      //inside
      }
      else if(lsSQL.indexOf("GROUP BY") > 0){
         lnIndex = lsSQL.indexOf("GROUP BY");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("HAVING") > 0){
         lnIndex = lsSQL.indexOf("HAVING");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("ORDER BY") > 0){
         lnIndex = lsSQL.indexOf("ORDER BY");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("LIMIT") > 0){
         lnIndex = lsSQL.indexOf("LIMIT");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else{
         lsSQL.append(" WHERE " + condition);
      }   
      return lsSQL.toString();
    }

    public static String getNextCode(
        String fsTableNme,
        String fsFieldNme,
        boolean fbYearFormat,
        java.sql.Connection foCon,
        String fsBranchCd){
        String lsNextCde="";
        int lnNext;
        String lsPref = fsBranchCd;

        String lsSQL = null;
        Statement loStmt = null;
        ResultSet loRS = null;

        if(fbYearFormat){
            try {
                if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }          
            
                loStmt = foCon.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                System.out.println(loRS.getString(1));
                lsPref = lsPref + loRS.getString(1).substring(2);
                System.out.println(lsPref);
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
            finally{
                close(loRS);
                close(loStmt);
            }
        }
      
        lsSQL = "SELECT " + fsFieldNme
                + " FROM " + fsTableNme
                + " ORDER BY " + fsFieldNme + " DESC "
                + " LIMIT 1";

        if(!lsPref.isEmpty())
            lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
      
        try {
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            if(loRS.next()){
               lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            }
            else
               lnNext = 0;

            lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            lsNextCde = "";
        }
        finally{
            close(loRS);
            close(loStmt);
        }

        return lsNextCde;
    }

    public static String getNextCode(
      String fsTableNme,
      String fsFieldNme,
      boolean fbYearFormat,
      java.sql.Connection foCon,
      String fsBranchCd,
      String fsFilter){
      String lsNextCde="";
      int lnNext;
      String lsPref = fsBranchCd;

      String lsSQL = null;
      Statement loStmt = null;
      ResultSet loRS = null;

      if(fbYearFormat){
         try {
            if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
               lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
            }else{
               //assume that default database is MySQL ODBC
               lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
            }          
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            loRS.next();
            lsPref = lsPref + loRS.getString(1).substring(2);
         } 
         catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            return "";
         }
         finally{
            close(loRS);
            close(loStmt);
         }
      }

      lsSQL = "SELECT " + fsFieldNme
           + " FROM " + fsTableNme
           + " ORDER BY " + fsFieldNme + " DESC "
           + " LIMIT 1";

      if(!lsPref.isEmpty())
         lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
         
      lsSQL = addCondition(lsSQL, fsFilter);
      
      try {
         loStmt = foCon.createStatement();
         loRS = loStmt.executeQuery(lsSQL);
         if(loRS.next()){
            lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
         }
         else
            lnNext = 0;


         lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

      } 
      catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
         lsNextCde = "";
      }
      finally{
         close(loRS);
         close(loStmt);
      }

      return lsNextCde;
   }

   public static String makeSelect(GEntity foObject) {
      StringBuilder lsSQL = new StringBuilder();
      lsSQL.append("SELECT ");
      lsSQL.append(foObject.getColumn(1));

      for(int lnCol=2; lnCol<=foObject.getColumnCount(); lnCol++){
          lsSQL.append(", " + foObject.getColumn(lnCol));
      }

      lsSQL.append( " FROM " + foObject.getTable());
      return lsSQL.toString();
   }

   public static Date dateAdd(Date date, int toAdd){
      return dateAdd(date, Calendar.DATE, toAdd);
   }
   
   public static Date dateAdd(Date date, int field, int toAdd){
      Calendar c1 = Calendar.getInstance();
      c1.setTime(date);
      c1.add(field, toAdd);
      return c1.getTime();
   }

   public static String[] splitName(String fsName){
      String laNames[] = {"", "", ""};
      fsName = fsName.trim();

      if(fsName.length() > 0){
         String laNames1[] = fsName.split(",");
         laNames[0] = laNames1[0].trim();
         laNames[1] = laNames1[1].trim();
         if(laNames1.length > 1){
            String lsFrstName = laNames1[1].trim();
            if(lsFrstName.length() > 0){
               laNames1 = lsFrstName.split("»");
               laNames[1] = laNames1[0];
               if(laNames1.length > 1)
                  laNames[2] = laNames1[1];
//               for(int x = 2; x < laNames1.length; x++)
//                   laNames[2] = laNames[2] + " " + laNames1[x];
            }
         }

         if(laNames[0].trim().length() == 0)
           laNames[0] = "%";
         if(laNames[1].trim().length() == 0)
           laNames[1] = "%";
         if(laNames[2].trim().length() == 0)
           laNames[2] = "%";
      }
      return laNames;
   }
   
   public static Map row2Map(ResultSet rs){
      Map map = new HashMap();
            
      try {
         if(rs.isAfterLast() || rs.isBeforeFirst()) return null;
            
         ResultSetMetaData rsmd = rs.getMetaData();
         int count = rsmd.getColumnCount();
            
         for (int i = 1; i <= count; i++) {
            String key = rsmd.getColumnName(i);
                
            switch(rsmd.getColumnType(i)){
               case java.sql.Types.ARRAY:
                  map.put(key, rs.getArray(i));
                  break;
               case java.sql.Types.BIGINT:
                  map.put(key, rs.getLong(i));
                  break;
               case java.sql.Types.REAL:
                  map.put(key, rs.getFloat(i));
                  break;
               case java.sql.Types.BOOLEAN:
               case java.sql.Types.BIT:    
                  map.put(key, rs.getBoolean(i));
                  break;
               case java.sql.Types.BLOB:
                  map.put(key, rs.getBlob(i));
                  break;
               case java.sql.Types.DOUBLE:
               case java.sql.Types.FLOAT:
                  map.put(key, rs.getDouble(i));
                  break;
               case java.sql.Types.INTEGER:
                  map.put(key, rs.getInt(i));
                  break;
               case java.sql.Types.NVARCHAR:
                  map.put(key, rs.getNString(i));
                  break;
               case java.sql.Types.VARCHAR:
               case java.sql.Types.CHAR:
               case java.sql.Types.LONGVARCHAR:
                  map.put(key, rs.getString(i));
                  break;
               case java.sql.Types.NCHAR:
               case java.sql.Types.LONGNVARCHAR:
                  map.put(key, rs.getNString(i));
                  break;
               case java.sql.Types.TINYINT:
                  map.put(key, rs.getByte(i));
                  break;
               case java.sql.Types.SMALLINT:
                  map.put(key, rs.getShort(i));
                  break;
               case java.sql.Types.DATE:
                  map.put(key, rs.getDate(i));
                  break;
               case java.sql.Types.TIME:
                  map.put(key, rs.getTime(i));
                  break;
               case java.sql.Types.TIMESTAMP:
                  map.put(key, rs.getTimestamp(i));
                  break;
               case java.sql.Types.BINARY:
               case java.sql.Types.VARBINARY:
                  map.put(key, rs.getBytes(i));
                  break;
               case java.sql.Types.LONGVARBINARY:
                  map.put(key, rs.getBinaryStream(i));
                  break;
               case java.sql.Types.CLOB:
                  map.put(key, rs.getClob(i));
                  break;
               case java.sql.Types.NUMERIC:
               case java.sql.Types.DECIMAL:
                  map.put(key, rs.getBigDecimal(i));
                  break;
               case java.sql.Types.DATALINK:
                  map.put(key, rs.getURL(i));
                  break;
               case java.sql.Types.REF:
                  map.put(key, rs.getRef(i));
                  break;
               case java.sql.Types.STRUCT:
               case java.sql.Types.DISTINCT:
               case java.sql.Types.JAVA_OBJECT:
                  map.put(key, rs.getObject(i));
                  break;
               default:
                  map.put(key, rs.getString(i));
               }
         }
            
      } catch (SQLException ex) {
         map = null;
      }
        
      return map;
   } 

    
   public static List rows2Map(ResultSet rs){
      List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

      try {
         while(rs.next()){
            Map map = row2Map(rs);
            if(map != null)
               list.add(map);
         }
      } catch (SQLException ex) {
         list = null;
      }

      return list;
   }
    
   //kalyptus - 2017.09.06 03:05pm
   //Converted into General SQL Insert
   public static String makeSQL(ResultSetMetaData rsmd, Map foMap, String fsTable, String fsExclude){
      StringBuilder lsSQL = new StringBuilder();
      StringBuilder lsNme = new StringBuilder();
      String column;
      try {
         int count = rsmd.getColumnCount();
         for(int i=1; i <= count; i++){
            column = rsmd.getColumnName(i);
            if(!fsExclude.contains(column)){
               lsNme.append(", " + column);
               lsSQL.append(", " + SQLUtil.toSQL(foMap.get(column)));
            }
         }
      } catch (SQLException ex) {
          lsSQL = new StringBuilder();
      }

      if(lsSQL.toString().isEmpty())
         return "";
      else
         return "INSERT INTO " 
               + fsTable + " (" + lsNme.toString().substring(1) + ")" 
               + " VALUES (" + lsSQL.toString().substring(1) + ")";

//            return "INSERT INTO " + fsTable + " SET" + lsSQL.toString().substring(1);
   }

   public static String makeSQL(ResultSetMetaData rsmd, Map foNewMap, Map foOldMap, String fsTable, String fsWhere, String fsExclude){
      StringBuilder lsSQL = new StringBuilder();
      String column;
      try {
         int count = rsmd.getColumnCount();
         for(int i=1; i <= count; i++){
            column = rsmd.getColumnName(i);
            if(!fsExclude.contains(column)){
               if(!SQLUtil.equalValue(foNewMap.get(column), foOldMap.get(column)))
                  lsSQL.append(", " + column + " = " + SQLUtil.toSQL(foNewMap.get(column)));
            }
         }
      } catch (SQLException ex) {
         lsSQL = new StringBuilder();
      }

      if(lsSQL.toString().isEmpty())
          return "";
      else
          return "UPDATE " + fsTable + " SET" + lsSQL.toString().substring(1) + " WHERE " + fsWhere;
  }
   
   //kalyptus - 2018.04.06 11:06am
   //create an instance of an object using the CLASSNAME...
   //Caveat - object's constructor should have an empty parameter...
   public static Object createInstance(String classname){
      Class<?> x;
      Object obj = null;
      try {
         x = Class.forName(classname);
         obj = x.newInstance();
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      } catch (InstantiationException ex) {
         ex.printStackTrace();
      } catch (IllegalAccessException ex) {
         ex.printStackTrace();
      }
      return obj;
   }
    
   //kalyptus - 2019.06.01 04:45pm
   //create an random number 
    public static int getRandom(int num){
        Random rand = new Random();
        return rand.nextInt(num) + 1;
    }
    
    public static int getRandom(int fnLow, int fnHigh){
        Random r = new Random();
        return r.nextInt(fnHigh - fnLow) + fnLow;
    }
    
    public static String StringToHex(String str) {
        char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(chars);
    }
    
    public static String HexToString(String hex) {
        String result = "";
        try {
            byte[] bytes = Hex.decodeHex(hex);
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Invalid Hex format!");
        }
        return result;
    }
}


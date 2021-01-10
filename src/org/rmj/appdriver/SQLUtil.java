/*
 * Copyright (c) 2001 - 2004 by Technische Universitaet Berlin, Technische
 * Universitaet Muenchen, Rheinisch-Westfaehlische Technische Hochschule Aachen,
 * Universitaet Potsdam.
 * Copyright (c) 2004 - 2007 by Technische Universitaet Berlin.
 *
 * This file is part of Mmjsql.
 *
 * Mmjsql is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Mmjsql is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Mmjsql; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * In addition, as a special exception, the copyright holders give permission to
 * link the code of Mmjsql to Sun's Java JRE, or to an equivalent Java JRE
 * from a different vendor.
 * ========================
 * Downloaded from: http://www.mumie.net/
 * ========================
 */

package org.rmj.appdriver;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.text.MaskFormatter;

/**
 * Static utility methods to deal with SQL code.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SQLUtil.java,v 1.4 2008/10/06 16:02:17 rassy Exp $</code>
 */
public class SQLUtil
{
    public static String FORMAT_LONG_DATE  = "MMMM dd, yyyy";
    public static String FORMAT_MEDIUM_DATE  = "MMM dd, yyyy";
    public static String FORMAT_SHORT_DATE = "yyyy-MM-dd";
    public static String FORMAT_TIMESTAMP  = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_SHORT_DATEX = "yyyyMMdd";
    public static String FORMAT_SHORT_YEAR = "yy";
    
  /**
   * Escapes single quotes and backslashes in <code>string</code>. This is done by replacing
   * each quote by two quotes, and each backslash by two backslashes.
   * 
   * @param string      string to be be escape.
   * @return            escape string value.
   */
  public static String quote (String string)
  {
    StringBuffer result = new StringBuffer();
    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        switch ( chars[i] )
          {
          case '\\':
             result.append("\\\\"); break;
          case '\'':
            result.append("''"); break;
          case '\"':
            result.append("\"").append("\""); break;
          default:
            result.append(chars[i]);
          }
      }
    return result.toString();
  }

  /**
   * Converts the specified string into a SQL literal. This means the string is quoted (see
   * method {@link #quote quote}) and enclosed in single quotes.
   * 
   * @param string      the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
  public static String toSQL (String string)
  {
     if(string instanceof String)
        return "'" + quote(string) + "'";
     else
        return null;
  }

  /**
   * Converts the specified timestamp into a SQL literal.
   * 
   * @param timestamp   the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
  public static String toSQL (Timestamp timestamp)
  {
     return toSQL((Date)timestamp);
  }

  /**
   * Converts the specified date into a SQL literal.
   * 
   * @param date        the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */

  public static String toSQL (Date date)
  {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String ret;
    if(date instanceof Date)
      ret = toSQL(sf.format(date));
    else
       ret = null;
    
    sf = null;
    return ret;    
  }

  /**
   * Converts the specified calendar object into a SQL literal.
   * 
   * @param calendar    the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
  public static String toSQL (Calendar calendar)
  {
    return toSQL(calendar.getTime());
  }

  /**
   * Converts an int into a SQL literal.
   * 
   * @param value       the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
   public static String toSQL (int value)
   {
      return Integer.toString(value);
   }

  /**
   * Converts a boolean into a SQL literal.
   * 
   * @param value       the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
   public static String toSQL (boolean value)
   {
      return (value ? "true" : "false");
   }

  /**
   * Converts the specified character object into a SQL literal.
   * 
   * @param value       the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
   public static String toSQL (Character value)
   {
      return toSQL("'" + value.toString() + "'" );
   }
  
  /**
   * Converts the specified object into a SQL literal.
   * 
   * @param object      the value to be enclose in a single quote.
   * @return            enclosed with single quote string.
   */
  public static String toSQL (Object object)
  {
      if ( object == null )
         return "NULL";
      if ( object instanceof Number )
         return object.toString();
      else if ( object instanceof Boolean )
         return toSQL(((Boolean)object).booleanValue());
      else if ( object instanceof String )
         return toSQL((String)object);
      else if ( object instanceof Timestamp )
         return toSQL((Timestamp)object);
      else if ( object instanceof Date )
         return toSQL((Date)object);
      else if ( object instanceof Calendar )
         return toSQL((Calendar)object);
      else if ( object instanceof Character )
         return toSQL((Character)object);
      else
         throw new IllegalArgumentException
            ("Can not convert to sql code: " + object);
}
   /**
    * Formats a date data type.
    * 
    * @param date    the date to be formatted.
    * @param format  the format to be used.
    * @return        the string representing the formatted date.
    */
   public static String dateFormat(Object date, String format){
      SimpleDateFormat sf = new SimpleDateFormat(format);
      String ret;
      if ( date instanceof Timestamp )
         ret = sf.format((Date)date);
      else if ( date instanceof Date )
         ret = sf.format(date);
      else if ( date instanceof Calendar ){
         Calendar loDate = (Calendar) date;
         ret = sf.format(loDate.getTime());
         loDate = null;
      }
      else
         ret = null;

      sf = null;
      return ret;
    }

   /**
    * Convert a string date into date value.
    * 
    * @param date       the string date to be converted to date.
    * @param format     the format/form of the string date.
    * @return           the date equivalent of the string date.
    */
   public static Date toDate(String date, String format){
      Date loDate = null;
      try{
         //Be sure to follow the format specified
         SimpleDateFormat sf = new SimpleDateFormat(format);
         loDate = sf.parse(date);
         sf = null;
      }
      catch(ParseException ex){
         ex.printStackTrace();
         //Nothing to do;
      }

      return loDate;
   }

   /**
    * Format a string value.
    * 
    * @param value      the string value to format.
    * @param format     the format/pattern to used.
    * @return           the formatted equivalent of the string value.
    */
   public static String strFormat(String value, String format){
      String ret;
      try {
         MaskFormatter mf = new MaskFormatter(format);
         mf.setValueContainsLiteralCharacters(false);
         ret = mf.valueToString(value);
         mf = null;
      } catch (ParseException ex) {
         ex.printStackTrace();
         ret = "";
      }
      return ret;
   }
    
   /**
    * Format a long value.
    * 
    * @param value      the long value to format.
    * @param format     the format/pattern to used.
    * @return           the formatted equivalent of the long value.
    */
   public static String strFormat(long value, String format){
      String ret;
      try {
         MaskFormatter mf = new MaskFormatter(format);
         mf.setValueContainsLiteralCharacters(false);
         ret = mf.valueToString(value);
         mf = null;
      } catch (ParseException ex) {
         ex.printStackTrace();
         ret = "";
      }

      return ret;
   }

   /**
    * Format a double value.
    * 
    * @param value      the double value to format.
    * @param format     the format/pattern to used.
    * @return           the formatted equivalent of the double value.  
    */ 
   public static String strFormat(double value, String format){
      String ret;
      try {
         MaskFormatter mf = new MaskFormatter(format);
         mf.setValueContainsLiteralCharacters(false);
         ret = mf.valueToString(value);
         mf = null;
      } catch (ParseException ex) {
         ex.printStackTrace();
         ret = "";
      }

      return ret;
   }
    
   public static String toStrValue(String value, String format){
      try {
         MaskFormatter mf = new MaskFormatter(format);
         mf.setValueContainsLiteralCharacters(false);
         return (String) mf.stringToValue(value);
      } catch (ParseException ex) {
         return "";
      }
   }
   
   /**
    * Compare the value of two object.
    * @param obj1    First Object.
    * @param obj2    Second Object.
    * @return        true if the same, otherwise false.
    */
   public static boolean equalValue(Object obj1, Object obj2)
   {
      if ( obj1 == null && obj2 == null)
         return true;
      else if((obj1 != null && obj2 == null) || (obj1 == null && obj2 != null))
         return false;  

      if ( obj1 instanceof Number && obj2 instanceof Number )
         return (new BigDecimal(obj1.toString()).compareTo(new BigDecimal(obj2.toString()))) == 0;
      else if ( obj1 instanceof Boolean && obj2 instanceof Boolean )
         return (Boolean)obj1 == (Boolean)obj2;
      else if ( obj1 instanceof String && obj2 instanceof String)
         return ((String)obj1).trim().equalsIgnoreCase(((String)obj2).trim()) ;
      else if ( obj1 instanceof Timestamp && obj2 instanceof Timestamp)
         return ((Timestamp)obj1).equals((Timestamp)obj1);
      else if ( obj1 instanceof Date && obj2 instanceof Date )
         return ((Date)obj1).equals(obj2);
      else if ( obj1 instanceof Calendar && obj2 instanceof Calendar )
         return ((Calendar)obj1).equals(obj2);
      else if ( obj1 instanceof Character && obj2 instanceof Character)
         return ((Character)obj1).equals(obj2);
      else
         return false;  
   }
}


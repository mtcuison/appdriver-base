/******************************************************************************
 *
 * Copyright (c) 1999-2005 AppGate Network Security AB. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as
 * defined in and that are subject to the MindTerm Public Source License,
 * Version 2.0, (the 'License'). You may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the MindTerm Public Source License
 * along with this software; see the file LICENSE.  If not, write to
 * AppGate Network Security AB, Otterhallegatan 2, SE-41118 Goteborg, SWEDEN
 *
 *****************************************************************************/

package org.rmj.appdriver;

/**
 * Static utility functions for trimming strings and converting byte
 * counts to strings.
 */
public final class StringUtil {

    /**
     * Trims space from the start of a string.
     *
     * @param str string to trim space characters from
     *
     * @return a new string without any leading space characters
     */
    public static String trimLeft(String str) {
        char[] val = str.toCharArray();
        int st = 0;
        while ((st < val.length) && (val[st] <= ' ')) {
            st++;
        }
        return str.substring(st);
    }

    /**
     * Trims space from the end of a string.
     *
     * @param str string to trim space characters from
     *
     * @return a new string without any trailing space characters
     */
    public static String trimRight(String str) {
        char[] val = str.toCharArray();
        int    end = val.length;
        while ((end > 0) && (val[end - 1] <= ' ')) {
            end--;
        }
        return str.substring(0, end);
    }

   /**
    * Replicates a string.
    * 
    * @param str     The string to replicate.
    * @param ctr     The number of times the string will be replicated.
    * @return        The replicated string value.
    */ 
   public static String replicate(String str, int ctr){
      StringBuilder s = new StringBuilder();
      if(ctr < 1)
         return "";

      for(int ln = 1;ln<=ctr;ln++)
         s.append(str);

      return s.toString();
   }

   /**
    * Checks whether string is convertible to number.
    * 
    * @param str     The string value to check.
    * @return        true if the string value is convertible, otherwise false.
    */
   public static boolean isNumeric(String str)
   {
     return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
   }   
}


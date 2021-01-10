/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmj.appdriver;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author sayso
 */
public class GLogger {
    private static String log_path;
    private static String log_file;
    private static Logger logger;
    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;

    static{
        log_path = System.getProperty("java.io.tmpdir");
        log_file = System.getProperty("sys.default.id");
        log_file = (log_file == null) ? "GRider" : log_file;

        logger = Logger.getLogger(log_file);

        try{
            fileTxt = new FileHandler(log_path + log_file  + ".log", true);

            formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        } catch (Exception e) {
            e.printStackTrace();
        }    
        
    }    

    public static void info(String cls, String mthd, String msg){
        logger.logp(Level.INFO, cls, mthd, msg);   
    }
    
    public static void warning(String cls, String mthd, String msg){
        logger.logp(Level.WARNING, cls, mthd, msg);   
    }

    public static void severe(String cls, String mthd, String msg){
        logger.logp(Level.SEVERE, cls, mthd, msg);   
    }
}

package org.rmj.appdriver;

/**
 *
 * @author Michael Cuison
 *      2020.11.26 - Started creating this object.
 *          
 */
public class Tokenize {
    public static String EncryptAuthToken(String fsEmployID, String fsMobileNo, String fsAuthLevl, String fsAuthEqlx){
        //HEX(AES_ENCRYPT(sEmployNo:sMobileNo:sAuthLevl:sAuthEqlx, employno))
        
        if (fsEmployID.isEmpty()) return "";
        if (fsMobileNo.isEmpty()) return "";
        if (fsAuthLevl.isEmpty() || !StringHelperMisc.isNumeric(fsAuthLevl)) return "";
        if (fsAuthEqlx.isEmpty() || !StringHelperMisc.isNumeric(fsAuthEqlx)) return "";
        
        String lsValue = fsEmployID + ":" + fsMobileNo + ":" + fsAuthLevl + ":" + fsAuthEqlx;
        
        lsValue = MySQLAESCrypt.Encrypt(lsValue, fsEmployID);
        lsValue = MiscUtil.StringToHex(lsValue);
        
        return lsValue;
    }
    
    public static String EncryptApprovalToken(String fsTransNox, String fsApprType, String fsRqstType, String fsEmployID){
        //HEX(AES_ENCRYPT(transnox + ":" + approval_type + ":" + reqst_type , employno))
        
        if (fsTransNox.isEmpty()) return "";
        if (fsApprType.isEmpty() || !StringHelperMisc.isNumeric(fsApprType)) return "";
        if (fsRqstType.isEmpty()) return "";
        if (fsEmployID.isEmpty()) return "";
        
        String lsValue = fsTransNox + ":" + fsApprType + ":" + fsRqstType;
        
        lsValue = MySQLAESCrypt.Encrypt(lsValue, fsEmployID);
        lsValue = MiscUtil.StringToHex(lsValue);
            
        return lsValue;
    }
    
    public static String DecryptToken(String fsValue, String fsEmployID){
        //AES_DECRYPT(HEX(enc_value,semployno))
        
        if (fsValue.isEmpty()) return "";
        if (fsEmployID.isEmpty()) return "";
        
        String lsValue = MiscUtil.HexToString(fsValue);
        
        lsValue = MySQLAESCrypt.Decrypt(lsValue, fsEmployID);
        
        return lsValue;
    }
}

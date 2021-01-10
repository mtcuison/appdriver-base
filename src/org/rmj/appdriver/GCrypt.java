/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rmj.appdriver;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class GCrypt {
   Cipher cipher;
   SecretKeySpec key;
   byte[] keyBytes;

   public GCrypt(byte[] keybytes){
      try {
         this.keyBytes = keybytes;
         Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

         this.key = new SecretKeySpec(this.keyBytes, "ARC4");
         this.cipher = Cipher.getInstance("ARC4", "BC");
      } catch (NoSuchAlgorithmException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchProviderException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public byte[] decrypt(byte[] input){
      try {
         cipher.init(Cipher.DECRYPT_MODE, key);
         byte[] plainText = new byte[input.length];
         int ctLength = cipher.update(input, 0, input.length, plainText, 0);
         ctLength += cipher.doFinal(plainText, ctLength);
         return plainText;
      } catch (IllegalBlockSizeException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ShortBufferException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (BadPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvalidKeyException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public byte[] encrypt(byte[] input){
      try {
         cipher.init(Cipher.ENCRYPT_MODE, key);
         byte[] cipherText = new byte[input.length];
         int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
         ctLength += cipher.doFinal(cipherText, ctLength);

         return cipherText;
      } catch (IllegalBlockSizeException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ShortBufferException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (BadPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvalidKeyException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public GCrypt(){
      try {
         Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
         this.keyBytes = "08220326".getBytes("ISO-8859-1");
//         this.keyBytes = "08220326".getBytes("UTF-16");
         this.key = new SecretKeySpec(this.keyBytes, "ARC4");
         this.cipher = Cipher.getInstance("ARC4", "BC");
      } catch (UnsupportedEncodingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchAlgorithmException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchProviderException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public GCrypt(String key){
      try {
         Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
         this.keyBytes = key.getBytes("ISO-8859-1");
//         this.keyBytes = key.getBytes("UTF-16");

         this.key = new SecretKeySpec(this.keyBytes, "ARC4");
         this.cipher = Cipher.getInstance("ARC4", "BC");
      } catch (UnsupportedEncodingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchAlgorithmException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchProviderException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   public String encrypt(String data){
      byte[] input;

      try {
         input = data.getBytes("ISO-8859-1");
//         input = data.getBytes("UTF-16");

         cipher.init(Cipher.ENCRYPT_MODE, key);
         byte[] cipherText = new byte[input.length];
         int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
         ctLength += cipher.doFinal(cipherText, ctLength);

         return new String(cipherText, "ISO-8859-1");
//         return new String(cipherText, "UTF-16");
      } catch (UnsupportedEncodingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalBlockSizeException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ShortBufferException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (BadPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvalidKeyException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
      return "";
   }

   public String decrypt(String data){
      byte[] input;

      try {
         input = data.getBytes("ISO-8859-1");
//         input = data.getBytes("UTF-16");
         cipher.init(Cipher.DECRYPT_MODE, key);
         byte[] plainText = new byte[input.length];
         int ctLength = cipher.update(input, 0, input.length, plainText, 0);
         ctLength += cipher.doFinal(plainText, ctLength);
         return new String(plainText, "ISO-8859-1");
//         return new String(plainText, "UTF-16");
      } catch (UnsupportedEncodingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalBlockSizeException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ShortBufferException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (BadPaddingException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvalidKeyException ex) {
         ex.printStackTrace();
         Logger.getLogger(GCrypt.class.getName()).log(Level.SEVERE, null, ex);
      }
      return "";
   }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sse_implementation_trial1;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecretKeys {

    private SecretKey sk_Index;
    private SecretKey sk_DB;
    private String fileName;

    public SecretKeys(SecretKey sk_Index, SecretKey sk_DB, String fileName) {
        this.sk_Index = sk_Index;
        this.sk_DB = sk_DB;
        storeToFile(fileName);
    }
    public SecretKeys(String FileName) {
        retrieveFromFile(FileName);
    }

    public void storeToFile(String fileName) {
        File file = new File(fileName);
        String fl = fileName + ".key";
        try {
            FileOutputStream fos = new FileOutputStream(fl);
            byte[] kb1 = sk_Index.getEncoded();
            byte[] kb2 = sk_DB.getEncoded();
            byte[] kb = new byte[kb1.length + kb2.length];
            System.arraycopy(kb1, 0, kb, 0, kb1.length);
            System.arraycopy(kb2, 0, kb, kb1.length, kb2.length);
            
            fos.write(kb);
            fos.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SecretKeys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecretKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrieveFromFile(String fileName) {
        try {
            String fl = fileName + ".key";
            
            FileInputStream fis = new FileInputStream(fl);
            int kl = fis.available();
            byte[] kb = new byte[kl];
            fis.read(kb);
            
            this.sk_Index = new SecretKeySpec(Arrays.copyOfRange(kb, 0, 16), "AES");
            this.sk_DB = new SecretKeySpec(Arrays.copyOfRange(kb, 16, 32), "AES"); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SecretKeys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecretKeys.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    

    public SecretKey getSk_Index() {
        return this.sk_Index;
    }

    public SecretKey getSk_DB() {
        return this.sk_DB;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.leads.processor.encrypt;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

//import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import eu.leads.processor.common.infinispan.InfinispanManager;
import sun.misc.BASE64Encoder;

/**
 *
 * @author John Demertzis
 */
public class ClientSide {

    private InfinispanManager manager = null;
    private int Bvalue;
    private int Svalue;
    private int N;
    private double k = 1.1;
    //private int lambda = 128;
    private SecretKey sk_T;
    private SecretKey secretKey_tuple;
    private int maximum_tuple_size = 120;
    private String SK_fileName = "";

    public ClientSide(int Svalue, double k, String SK_fileName) {
        this.Svalue = Svalue;
        this.k = k;
        this.SK_fileName = SK_fileName;
    }

    public ClientSide(int Svalue,double k,String SK_fileName, InfinispanManager manager){
        this.Svalue = Svalue;
        this.k = k;
        this.SK_fileName = SK_fileName;
        this.manager = manager;
    }
    public ClientSide(String SK_fileName) {
        this.SK_fileName = SK_fileName;
    }


    public int getBvalue() {
        return Bvalue;
    }

    public int getSvalue() {
        return Svalue;
    }

    public byte[] xor(byte[] key, byte[] plaintext) {
        byte[] retVal = new byte[plaintext.length];
        int keySize = key.length; // in bytes
        int byteArraySize = plaintext.length; // in bytes
        for (int i = 0; i < byteArraySize; i += keySize) {
            int end = (i + keySize - 1);
            if (end > plaintext.length) {
                end = plaintext.length - 1;
            }
            for (int j = i; j <= end; j++) {
                retVal[j] = (byte) (plaintext[j] ^ key[(j % key.length)]);
            }
        }
        return retVal;
    }

    public CStore Setup(Connection conn, String tableName, String column) throws IOException, InvalidAlgorithmParameterException {
        Map<String, ArrayList<String>> index = null;
         if(manager != null)
             index = manager.getPersisentCache("tmp.index.cache");
         else
             index = new HashMap<String, ArrayList<String>>();
        Map<String, Etuple> db = null;
        if(manager != null){
            db = manager.getPersisentCache("tmp.db.cache");
        }
        else {
            db = new HashMap<String, Etuple>();
        }
        //String line = null;
        //String lineArray[];
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();

        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            ResultSet resultsCount = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmdCount = resultsCount.getMetaData();
            int counter = 0;
            int max = 0;
            int numberColsCount = rsmdCount.getColumnCount();
            String strline_max = "";
            while (resultsCount.next()) {
                counter++;
                strline_max = "";
                strline_max = resultsCount.getString(1);//@@
                for (int i = 2; i <= numberColsCount; i++) {//@@
                    strline_max = strline_max.concat("," + resultsCount.getString(i));
                }
                if (strline_max.length() > max) {
                    max = strline_max.length();
                }
            }
            max = max + 3;

            resultsCount.close();
            stmt.close();
            this.N = counter;
            this.maximum_tuple_size = max;

            this.Bvalue = (int) Math.ceil(k * N / Svalue);
            //System.out.println("N = "+counter);
            //System.out.println("max= "+max);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey_tuple = keyGen.generateKey();

            int padding = (int) Math.pow(10, (this.N + "").length());
            stmt = conn.createStatement();
            //ResultSet results = stmt.executeQuery("select * from " + tableName + " T0 order by "+column);
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            int id = 0;
            String strline = "";
            while (results.next()) {
                strline = "";
                strline = results.getString(1);//@@
                for (int i = 2; i <= numberCols; i++) {//@@
                    strline = strline.concat("," + results.getString(i));
                }
                int icolumn = results.findColumn(column);

                if (index.containsKey(results.getString(icolumn))) {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index = index.get(results.getString(icolumn));
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(results.getString(icolumn), arrayList_index);
                } else {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(results.getString(icolumn), arrayList_index);
                }

                //line-tuple padding
                StringBuilder sb = new StringBuilder(strline);
                String ret;
                int len = strline.length();

                char[] ch = new char[maximum_tuple_size - len];
                Arrays.fill(ch, ' ');
                sb.append(ch);
                ret = sb.toString();
                strline = ret;

                int AES_KEYLENGTH = 128;
                byte[] iv = new byte[AES_KEYLENGTH / 8];
                SecureRandom prng = new SecureRandom();
                prng.nextBytes(iv);

                Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey_tuple, new IvParameterSpec(iv));
                strDataToEncrypt = strline;
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                strCipherText = new BASE64Encoder().encode(byteCipherText);
                Etuple etuple = new Etuple(byteCipherText, iv);
                db.put(Integer.toString(id + padding), etuple);
                id++;
            }
            results.close();
            stmt.close();

            N = id;
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }
        if(manager != null)
        {

        }
        CStore cs = new CStore(db, TSetSetup(index), this.Bvalue, this.Svalue);
        SecretKeys sk = new SecretKeys(this.sk_T, this.secretKey_tuple, SK_fileName);

        this.secretKey_tuple = null;
        this.sk_T = null;

        return cs;

    }

    public CStore Setup(String fileName, int columnNumber) throws IOException, InvalidAlgorithmParameterException {
        Map<String, ArrayList<String>> index =null;
        Map<String, Etuple> db = null;
        if(manager != null){
            index = manager.getCacheManager().getCache("tmp.index.cache");
        }else{
            index = new HashMap<String, ArrayList<String>>();
        }
        if(manager != null){
            db =manager.getCacheManager().getCache("tmp.db.cache");
        }
        else{
             db = new HashMap<String, Etuple>();
        }

        String line = null;
        String lineArray[];
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();

        try {
            FileReader fileReaderCount = new FileReader(fileName);
            BufferedReader bufferedReaderCount = new BufferedReader(fileReaderCount);


            int counter = 0;
            int max = 0;

            while ((line = bufferedReaderCount.readLine()) != null) {
                if (max < line.length()) {
                    max = line.length();
                }
                counter++;
            }
            max = max + 3;
            this.N = counter;
            this.maximum_tuple_size = max;

            this.Bvalue = (int) Math.ceil(k * N / Svalue);
            //System.out.println("N = "+counter);
            //System.out.println("max= "+max);

            bufferedReaderCount.close();
            fileReaderCount.close();

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey_tuple = keyGen.generateKey();

            int padding = (int) Math.pow(10, (this.N + "").length());

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int id = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineArray = line.split(",");
                if (index.containsKey(lineArray[columnNumber])) {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index = index.get(lineArray[columnNumber]);
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[columnNumber], arrayList_index);
                } else {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[columnNumber], arrayList_index);
                }
                //line-tuple padding
                StringBuilder sb = new StringBuilder(line);
                String ret;
                int len = line.length();
                char[] ch = new char[maximum_tuple_size - len];
                Arrays.fill(ch, ' ');
                sb.append(ch);
                ret = sb.toString();
                line = ret;

                int AES_KEYLENGTH = 128;
                byte[] iv = new byte[AES_KEYLENGTH / 8];
                SecureRandom prng = new SecureRandom();
                prng.nextBytes(iv);

                Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey_tuple, new IvParameterSpec(iv));
                strDataToEncrypt = line;
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                strCipherText = new BASE64Encoder().encode(byteCipherText);
                Etuple etuple = new Etuple(byteCipherText, iv);
                db.put(Integer.toString(id + padding), etuple);
                id++;
            }
            N = id;
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }

        CStore cs = new CStore(db, TSetSetup(index), this.Bvalue, this.Svalue);
        SecretKeys sk = new SecretKeys(this.sk_T, this.secretKey_tuple, SK_fileName);

        this.secretKey_tuple = null;
        this.sk_T = null;
        return cs;
    }

    public CStore Setup(Connection conn, String tableName, String column, int N, int maxtuplesize) throws IOException, InvalidAlgorithmParameterException {
        Map<String, ArrayList<String>> index =null;
        Map<String, Etuple> db = null;
        if(manager != null){
            index = manager.getPersisentCache("tmp.index.cache");
        }else{
            index = new HashMap<String, ArrayList<String>>();
        }
        if(manager != null){
            db = manager.getPersisentCache("tmp.index.cache");
        }
        else{
            db = new HashMap<String, Etuple>();
        }
        //String line = null;
        //String lineArray[];
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();

        Statement stmt = null;

        try {
            this.N = N;
            this.maximum_tuple_size = maxtuplesize;

            this.Bvalue = (int) Math.ceil(k * N / Svalue);
            //System.out.println("N = "+counter);
            //System.out.println("max= "+max);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey_tuple = keyGen.generateKey();

            int padding = (int) Math.pow(10, (this.N + "").length());
            stmt = conn.createStatement();
            //ResultSet results = stmt.executeQuery("select * from " + tableName + " T0 order by "+column);
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            int id = 0;
            String strline = "";
            while (results.next()) {
                strline = "";
                strline = results.getString(1);//@@
                for (int i = 2; i <= numberCols; i++) {//@@
                    strline = strline.concat("," + results.getString(i));
                }
                int icolumn = results.findColumn(column);

                if (index.containsKey(results.getString(icolumn))) {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index = index.get(results.getString(icolumn));
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(results.getString(icolumn), arrayList_index);
                } else {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(results.getString(icolumn), arrayList_index);
                }

                //line-tuple padding
                StringBuilder sb = new StringBuilder(strline);
                String ret;
                int len = strline.length();

                char[] ch = new char[maximum_tuple_size - len];
                Arrays.fill(ch, ' ');
                sb.append(ch);
                ret = sb.toString();
                strline = ret;

                int AES_KEYLENGTH = 128;
                byte[] iv = new byte[AES_KEYLENGTH / 8];
                SecureRandom prng = new SecureRandom();
                prng.nextBytes(iv);

                Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey_tuple, new IvParameterSpec(iv));
                strDataToEncrypt = strline;
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                strCipherText = new BASE64Encoder().encode(byteCipherText);
                Etuple etuple = new Etuple(byteCipherText, iv);
                db.put(Integer.toString(id + padding), etuple);
                id++;
            }
            results.close();
            stmt.close();

            N = id;
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }

        CStore cs = new CStore(db, TSetSetup(index), this.Bvalue, this.Svalue);
        SecretKeys sk = new SecretKeys(this.sk_T, this.secretKey_tuple, SK_fileName);

        this.secretKey_tuple = null;
        this.sk_T = null;

        return cs;

    }

    public CStore Setup(String fileName, int columnNumber, int N, int maxtuplesize) throws IOException, InvalidAlgorithmParameterException {
        Map<String, ArrayList<String>> index =null;
        Map<String, Etuple> db = null;
        if(manager != null){
            index = manager.getPersisentCache("tmp.index.cache");
        }else{
            index = new HashMap<String, ArrayList<String>>();
        }
        if(manager != null){
            db = manager.getPersisentCache("tmp.index.cache");
        }
        else{
            db = new HashMap<String, Etuple>();
        }
        String line = null;
        String lineArray[];
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();

        try {

            this.N = N;
            this.maximum_tuple_size = maxtuplesize;

            this.Bvalue = (int) Math.ceil(k * N / Svalue);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey_tuple = keyGen.generateKey();

            int padding = (int) Math.pow(10, (this.N + "").length());

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int id = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineArray = line.split(",");
                if (index.containsKey(lineArray[columnNumber])) {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index = index.get(lineArray[columnNumber]);
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[columnNumber], arrayList_index);
                } else {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[columnNumber], arrayList_index);
                }
                //line-tuple padding
                StringBuilder sb = new StringBuilder(line);
                String ret;
                int len = line.length();
                char[] ch = new char[maximum_tuple_size - len];
                Arrays.fill(ch, ' ');
                sb.append(ch);
                ret = sb.toString();
                line = ret;

                int AES_KEYLENGTH = 128;
                byte[] iv = new byte[AES_KEYLENGTH / 8];
                SecureRandom prng = new SecureRandom();
                prng.nextBytes(iv);

                Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey_tuple, new IvParameterSpec(iv));
                strDataToEncrypt = line;
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                strCipherText = new BASE64Encoder().encode(byteCipherText);
                Etuple etuple = new Etuple(byteCipherText, iv);
                db.put(Integer.toString(id + padding), etuple);
                id++;
            }
            N = id;
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }

        CStore cs = new CStore(db, TSetSetup(index), this.Bvalue, this.Svalue);
        SecretKeys sk = new SecretKeys(this.sk_T, this.secretKey_tuple, SK_fileName);

        this.secretKey_tuple = null;
        this.sk_T = null;
        return cs;
    }

    public Map<Integer, Record[]> TSetSetup(Map<String, ArrayList<String>> index) throws InvalidAlgorithmParameterException, UnsupportedEncodingException {
        //HashMap<String, ArrayList<String>> index = cs.getTSet();

        Bvalue = (int) Math.ceil(k * N / Svalue);
        int total = Svalue * Bvalue;
        int i = 0;
        int j = 0;
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);


        Map<Integer, Record[]> Tset =  null;
        if(manager != null){
            Tset = manager.getPersisentCache("tmp.tset.cache");
        }
        else {
          Tset = new HashMap();
        }
        ArrayList[] Free = new ArrayList[Bvalue];

        for (i = 0; i < Bvalue; i++) {
            Record[] rc = new Record[Svalue];
            Free[i] = new ArrayList<String>();
            for (j = 0; j < Svalue; j++) {
                rc[j] = new Record();
                rc[j].Record();
                Free[i].add(j, Integer.toString(j));
            }
            Tset.put(i, rc);
        }
        //Choose a ranodm key Kt of Prf F'
        try {
            //PRF F'
            KeyGenerator keyGen;
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            sk_T = secretKey;

            Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            int metrima = 0;
            Iterator<String> iter = index.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList = index.get(key);

                //Encryption
                strDataToEncrypt = key;
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                // stag = F(kt,w)
                strCipherText = new BASE64Encoder().encode(byteCipherText);

                for (j = 0; j < arrayList.size(); j++) {
                    metrima++;
                    byte[] KeyBytes = strCipherText.getBytes("UTF-8");
                    MessageDigest sha = MessageDigest.getInstance("SHA-1");
                    KeyBytes = sha.digest(KeyBytes);
                    KeyBytes = Arrays.copyOf(KeyBytes, 16); // use only first 128 bit
                    SecretKeySpec secretKeySpec = new SecretKeySpec(KeyBytes, "AES");
                    Cipher aesCipherForEncryption2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    aesCipherForEncryption2.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
                    //Encryption PRF F
                    String strDataToEncrypt2 = Integer.toString(j);
                    byte[] byteDataToEncrypt2 = strDataToEncrypt2.getBytes();
                    byte[] byteCipherText2 = aesCipherForEncryption2.doFinal(byteDataToEncrypt2);
                    String strCipherText2 = new BASE64Encoder().encode(byteCipherText2);

                    // (b,L,K) = H(F(stag,i)) <---> (b,L,K) = H(strCipherText2)
                    byte[] PRFBytes = strCipherText2.getBytes("UTF-8");
                    MessageDigest hashF = MessageDigest.getInstance("SHA-512");
                    PRFBytes = hashF.digest(PRFBytes);

                    byte[] b = Arrays.copyOfRange(PRFBytes, 0, 8);
                    byte[] L = Arrays.copyOfRange(PRFBytes, 8, 24);
                    byte[] K = Arrays.copyOfRange(PRFBytes, 24, 32);
                    int bint;
                    bint = Math.abs(java.nio.ByteBuffer.wrap(b).getInt());
                    bint = (bint + 1) % Bvalue;


                    //if Free[b] is empty 
                    if (Free[bint].isEmpty()) {
                        return TSetSetup(index);
                    }
                    //Unifrom from free[b]
                    RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(java.nio.ByteBuffer.wrap(Arrays.copyOfRange(PRFBytes, 0, 32)).getInt());

                    Uniform unif = new Uniform(generator);
                    int coin = unif.nextIntFromTo(0, Free[bint].size() - 1);
                    int Sindex = Integer.parseInt(Free[bint].get(coin).toString());
                    Free[bint].remove(coin);

                    char beta = '1';
                    if (j == arrayList.size() - 1) {
                        beta = '0';
                    }
                    Record[] rcd = new Record[Svalue];
                    rcd = Tset.get(bint);

                    String Ls = new String(L);
                    rcd[Sindex].setLabel(Ls);
                    String plaintext = new String();
                    plaintext = (beta + "" + arrayList.get(j));
                    byte[] cipher = xor(K, plaintext.getBytes());
                    String ciphert = new String(cipher);
                    rcd[Sindex].setValue(cipher);
                    Tset.put(bint, rcd);
                }
            }

        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }
        return Tset;

    }

    public String TSetGetTag(String key) throws InvalidAlgorithmParameterException {
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        SecretKeys sk = new SecretKeys(this.SK_fileName);
        this.secretKey_tuple = sk.getSk_DB();
        this.sk_T = sk.getSk_Index();


        try {
            Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, sk_T, ivspec);
            //Encryption
            strDataToEncrypt = key;
            byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
            byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
            // stag = F(kt,w)
            strCipherText = new BASE64Encoder().encode(byteCipherText);
            return strCipherText;

        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        }
        return null;
    }

    public Map<String, ArrayList<String>> Decrypt_Answer(Map<String, ArrayList<Etuple>> encResult) {
        SecretKeys sk = new SecretKeys(this.SK_fileName);
        this.secretKey_tuple = sk.getSk_DB();
        this.sk_T = sk.getSk_Index();

        if (encResult == null) {
            System.out.println("No results");
            return null;
        }
        ArrayList<Etuple> result = encResult.get("result");
        ArrayList<String> DecResult = new ArrayList<String>();
        try {
            Cipher aesCipherForDecryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
            String strDecryptedText = new String();
            for (int i = 0; i < result.size(); i++) {

                aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey_tuple, new IvParameterSpec(result.get(i).getIV()));
                byte[] byteDecryptedText;
                byteDecryptedText = aesCipherForDecryption.doFinal(result.get(i).getCiphertext());
                strDecryptedText = new String(byteDecryptedText);
                DecResult.add(strDecryptedText);
                System.out.println(strDecryptedText);
            }
        } catch (NoSuchAlgorithmException noSuchAlgo) {
            System.out.println(" No Such Algorithm exists " + noSuchAlgo);
        } catch (NoSuchPaddingException noSuchPad) {
            System.out.println(" No Such Padding exists " + noSuchPad);
        } catch (InvalidKeyException invalidKey) {
            System.out.println(" Invalid Key " + invalidKey);
        } catch (BadPaddingException badPadding) {
            System.out.println(" Bad Padding " + badPadding);
        } catch (IllegalBlockSizeException illegalBlockSize) {
            System.out.println(" Illegal Block Size " + illegalBlockSize);
        } catch (InvalidAlgorithmParameterException invalidParam) {
            System.out.println(" Invalid Parameter " + invalidParam);
        }
        Map<String, ArrayList<String>> output = null;
        if(manager != null){
            output = manager.getPersisentCache("tmp.output.cache");
        }
        else{
            output = new HashMap<String, ArrayList<String>>();
        }

        output.put("Decrypted Result", DecResult);
        return output;
    }
}


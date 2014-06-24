/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/**
 *
 * @author John Demertzis
 */
public class ClientSide {

    private int Bvalue;
    private int Svalue = 6000;
    private int N;
    private double k = 1.1;
    private int lambda = 128;
    private SecretKey sk_T;
    private SecretKey secretKey_tuple;
    private int maximum_tuple_size=120;

    public ClientSide(int Svalue, double k, int N, int lamda, int maximum_tuple_size) {
        this.Svalue = Svalue;
        this.k = k;
        this.lambda = lamda;
        this.Bvalue = (int) Math.ceil(k * N / Svalue);
        this.maximum_tuple_size = maximum_tuple_size;
    }

    public ClientSide() {
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

    public CStore DB_preprossesing(String fileName, int NumberOfTuples) throws IOException, InvalidAlgorithmParameterException {
        HashMap<String, ArrayList<String>> index = new HashMap<String, ArrayList<String>>();
        HashMap<String, Etuple> db = new HashMap<String, Etuple>();
        String line = null;
        String lineArray[];
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();
        try {
            
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey_tuple = keyGen.generateKey();

            int padding = (int) Math.pow(10, (NumberOfTuples + "").length());

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int id = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineArray = line.split(",");
                if (index.containsKey(lineArray[3])) {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index = index.get(lineArray[3]);
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[3], arrayList_index);
                } else {
                    ArrayList<String> arrayList_index = new ArrayList<String>();
                    arrayList_index.add(Integer.toString(id + padding));
                    index.put(lineArray[3], arrayList_index);
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

        CStore cs = new CStore(db, index);
        return cs;
    }

    public HashMap<Integer, Record[]> TSetSetup(CStore cs) throws InvalidAlgorithmParameterException, UnsupportedEncodingException {
        HashMap<String, ArrayList<String>> index = cs.getTSet();
      
        Bvalue = (int) Math.ceil(k * N / Svalue);
        int total = Svalue * Bvalue;
        int i = 0;
        int j = 0;
        String strDataToEncrypt = new String();
        String strCipherText = new String();
        String strDecryptedText = new String();
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        HashMap<Integer, Record[]> Tset = new HashMap();
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
            keyGen.init(lambda);
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
                        return TSetSetup(cs);
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

    public HashMap<String, ArrayList<String>> Decrypt_Answer(HashMap<String, Etuple> EDB, HashMap<String, ArrayList<String>> encResult) {
        ArrayList<String> result = encResult.get("Encrypted Result");
        ArrayList<String> DecResult = new ArrayList<String>();
        try {
            Cipher aesCipherForDecryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
            String strDecryptedText = new String();
            for (int i = 0; i < result.size(); i++) {
                aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey_tuple, new IvParameterSpec(EDB.get(result.get(i)).getIV()));
                byte[] byteDecryptedText;
                byteDecryptedText = aesCipherForDecryption.doFinal(EDB.get(result.get(i)).getCiphertext());
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
        HashMap<String, ArrayList<String>> output = new HashMap<String, ArrayList<String>>();
        output.put("Decrypted Result", DecResult);
        return output;
    }
}

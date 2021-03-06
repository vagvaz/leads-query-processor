/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse_implementation_trial1;

import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author John Demertzis
 */
public class ServerSide {

    private int Bvalue;
    private int Svalue;


    public ServerSide(int Svalue, int Bvalue) {
        this.Svalue = Svalue;
        this.Bvalue = Bvalue;
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

    public HashMap<String, ArrayList<Etuple>> TSetRetrieve(CStore cs, String token)
        throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        HashMap<String, Etuple> EDB = cs.getEDB(); //The encrypted Database
        HashMap<Integer, Record[]> TSet = cs.getTSet(); //the inverted index

        ArrayList<Etuple> result_list = new ArrayList<Etuple>();
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        char beta = '1';
        int i = 0;
        boolean flag = false;
        while (beta == '1') {
            try {
                byte[] KeyBytes = token.getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                KeyBytes = sha.digest(KeyBytes);
                KeyBytes = Arrays.copyOf(KeyBytes, 16); // use only first 128 bit
                SecretKeySpec secretKeySpec = new SecretKeySpec(KeyBytes, "AES");
                Cipher aesCipherForEncryption2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipherForEncryption2.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
                //Encryption PRF F
                String strDataToEncrypt2 = Integer.toString(i);
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

                String Ls = new String(L);
                int bint;
                bint = Math.abs(java.nio.ByteBuffer.wrap(b).getInt());
                bint = (bint + 1) % Bvalue;

                Record[] tmp_rcd = TSet.get(bint);
                for (int j = 0; j < Svalue; j++) {
                    if (tmp_rcd[j].getLabel().equals(Ls)) {
                        byte[] plaintext = xor(K, tmp_rcd[j].getValue());
                        String res = new String(plaintext);
                        result_list.add(EDB.get(res.substring(1)));
                        //result_list.add(res.substring(1));
                        beta = res.charAt(0);
                        flag = true;
                    }
                }
                if (flag == false) {
                    return null;
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
            i++;
        }
        HashMap<String, ArrayList<Etuple>> result = new HashMap<String, ArrayList<Etuple>>();

        result.put("Encrypted Result", result_list);
        return result;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/**
 *
 * @author John Demertzis
 */
public class ServerSide {

    private int Bvalue;
    private int Svalue = 6000;
    private double k = 1.1;

    public ServerSide(int Svalue,double k,int N) {
        this.Svalue=Svalue;
        this.k=k;
        this.Bvalue = (int) Math.ceil(k * N / Svalue);
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

    public HashMap<String, ArrayList<String>> TSetRetrieve(String token, HashMap<Integer, Record[]> TSet) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        ArrayList<String> result_list = new ArrayList<String>();
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        char beta = '1';
        int i = 0;
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
                        result_list.add(res.substring(1));
                        beta = res.charAt(0);
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
            i++;
        }
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        result.put("Encrypted Result", result_list);
        return result;
    }
}

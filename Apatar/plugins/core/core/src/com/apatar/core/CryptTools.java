/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.core;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.ibm.misc.BASE64Decoder;
import com.ibm.misc.BASE64Encoder;


public class CryptTools {

    private static String key = "VlShxxx345xxxxxa";
    private static SecretKeySpec keySpec;
    private static Cipher cipher;
    static {
    	try {
			keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			cipher = Cipher.getInstance("AES");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
    }
    public static String encrypt(String s) {
        String result = s;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result =  new BASE64Encoder().encode(cipher.doFinal(s.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decrypt(String s) {
        String result = s;
        if (s==null || s.length()<12) return s;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            result = new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(""+s)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}







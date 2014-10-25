/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse_implementation_trial1;


import java.io.Serializable;

/**
 * @author John Demertzis
 */
public class Etuple implements Serializable {
    private byte[] ciphertext;
    private byte[] IV = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public Etuple(byte[] ciphertext, byte[] IV) {
        this.ciphertext = ciphertext;
        this.IV = IV;
    }

    public Etuple() {
    }

    public byte[] getCiphertext() {
        return this.ciphertext;
    }

    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public byte[] getIV() {
        return this.IV;
    }

    public void setIV(byte[] IV) {
        this.IV = IV;
    }
}

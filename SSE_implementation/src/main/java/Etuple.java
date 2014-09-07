/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse_implementation_trial1;

/**
 *
 * @author John Demertzis
 */
public class Etuple {
    private byte[] ciphertext;
    private byte[] IV = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    
    public Etuple(byte[] ciphertext, byte[] IV){
        this.ciphertext = ciphertext;
        this.IV = IV;
    }
    
    public Etuple(){
    }
    
    public byte[] getCiphertext(){
        return this.ciphertext;
    }
    public byte[] getIV(){
        return this.IV;
    }
    public void setCiphertext(byte[] ciphertext){
        this.ciphertext = ciphertext;
    }
    public void setIV(byte[] IV){
        this.IV = IV;
    }   
}

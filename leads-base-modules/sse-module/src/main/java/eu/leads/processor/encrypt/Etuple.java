/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.leads.processor.encrypt;


import org.vertx.java.core.json.JsonObject;

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

    public JsonObject toJson(){
       JsonObject result = new JsonObject();
       result.putBinary("cipher",ciphertext);
       result.putBinary("iv",IV);
       return result;
    }

    public Etuple fromJson(String json){
       JsonObject object= new JsonObject(json);
       fromJson(object);
       return this;
    }
   public void fromJson(JsonObject object)
   {
      ciphertext = object.getBinary("ciphertext");
      IV = object.getBinary("iv");
   }
}

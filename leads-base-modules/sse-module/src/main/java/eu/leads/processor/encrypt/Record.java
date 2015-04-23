/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.leads.processor.encrypt;


import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;

public class Record implements Serializable{
    private String label;
    private byte[] value = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public void Record() {
        this.label = "0";
        //this.value = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public void Record(String label) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

   public JsonObject toJson(){
      JsonObject result = new JsonObject();
      result.putString("label",label);
      result.putBinary("value",value);
      return result;
   }

   public void fromJson(JsonObject object){
      this.label = object.getString("label");
      this.value = object.getBinary("value");
   }

   public void fromJson(String json){
      JsonObject object = new JsonObject(json);
      fromJson(object);
   }
}

package eu.leads.processor.core.json;

/**
 * Created by angelos on 02/04/15.
 */

import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by angelos on 22/01/15.
 */
public class Tuple extends DataType implements Serializable {

    public Tuple(){
        super();
    }

    public Tuple(String value) {
        this.data = new JsonObject(value);
    }

    public Tuple(Tuple tl, Tuple tr, ArrayList<String> ignoreColumns) {
        super(tl.asJsonObject());

        if(ignoreColumns != null) {
            for (String field : ignoreColumns) {
                if (data.containsField(field))
                    data.removeField(field);
            }
            tr.removeAtrributes(ignoreColumns);
        }
        data.mergeIn(tr.asJsonObject());
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(data.toString());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        data = new JsonObject((String) in.readObject());
    }

    private void readObjectNoData(){
        data = new JsonObject();
    }

    public String asString() {
        return data.toString();
    }

    public void setAttribute(String attributeName, String value) {
        data.putString(attributeName,value);
    }
    public void setNumberAttribute(String attributeName, Number value){
        data.putNumber(attributeName,value);
    }
    public void  setBooleanAttribute(String attributeName, boolean value){
        data.putBoolean(attributeName,value);
    }
    public void setObjectAttribute(String attributeName, JsonObject value){
        data.putObject(attributeName,value);
    }

    public String getAttribute(String column) {
        return data.getString(column).toString();
    }
    public Number getNumberAttribute(String column){return data.getNumber(column);}
    public boolean getBooleanAttribute(String column){return data.getBoolean(column);}
    public JsonObject getObjectAttribute(String column){return data.getObject(column);}
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return  data.toString();
    }

    public void keepOnly(List<String> columns) {
        Set<String> fields = new HashSet<>();
        fields.addAll(data.getFieldNames());
        Set<String> keep = new HashSet<>();
        keep.addAll(columns);

        for (String s : fields) {
            if(!keep.contains(s)){
                data.removeField(s);
            }
        }
    }

    public void removeAtrributes(List<String> columns) {
        for(String column : columns)
            data.removeField(column);
    }

    public String toPresentString() {
        return data.toString();
    }

    /**
     * Getter for property 'fieldNames'.
     *
     * @return Value for property 'fieldNames'.
     */
    public Set<String> getFieldNames() {
        return data.getFieldNames();
    }

    public boolean hasField(String attribute) {
        return data.containsField(attribute);
    }

    public void removeAttribute(String field) {
        data.removeField(field);
    }

    public void renameAttribute(String oldName, String newName) {
        if(oldName == newName)
            return;
        Object value = data.getValue(oldName);
        data.removeField(oldName);
        data.putValue(newName,value);
    }

    public Object getGenericAttribute(String attribute) {
        return data.getValue(attribute);
    }

    public void setAttribute(String name, Object tupleValue) {
        data.putValue(name,tupleValue);
    }

    public void renameAttributes(Map<String, List<String>> toRename) {
        for(Map.Entry<String,List<String>> entry : toRename.entrySet()){
            {
                Object value = getGenericAttribute(entry.getKey());
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if(i==0){
                        renameAttribute(entry.getKey(),entry.getValue().get(i));
                    }
                    else{
                        setAttribute(entry.getValue().get(i),value);
                    }
                }
            }
        }
    }
}
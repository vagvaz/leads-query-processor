package eu.leads.processor.core;

import com.mongodb.util.JSON;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

public class Tuple extends DataType_bson implements Serializable{

    static transient BasicBSONEncoder encoder = new BasicBSONEncoder();
    static transient BasicBSONDecoder decoder = new BasicBSONDecoder();

    public Tuple(){
        super();
    }

    public Tuple(String value) {
        this.data = new BasicBSONObject();
        this.data = (BSONObject) JSON.parse(value);
    }

    public Tuple(Tuple tl, Tuple tr, ArrayList<String> ignoreColumns) {
        super(tl.data);

        if(ignoreColumns != null) {
            for (String field : ignoreColumns) {
                if (data.containsField(field))
                    data.removeField(field);
            }
            tr.removeAtrributes(ignoreColumns);
        }
        data.putAll(tr.data);
    }
//
//  public Tuple(Tuple tmp) {
//
//  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // Serialize it
//        BasicBSONEncoder encoder = new BasicBSONEncoder();
        byte[] array = encoder.encode(data);
        out.write(array);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        // Deserialize it
//        BasicBSONDecoder decoder = new BasicBSONDecoder();
        data = decoder.readObject(in);
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

    public String asString() {
        return data.toString();
    }

    public String toString() {
        return data.toString();
    }

    public Set<String> getFieldSet() {
        return data.keySet();
    }

    public void setAttribute(String attributeName, String value) {
        data.put(attributeName,value);
    }

    public void setNumberAttribute(String attributeName, Number value){
        data.put(attributeName,value);
    }

    public String getAttribute(String column) {
        return data.get(column).toString();
    }

    public Number getNumberAttribute(String column){
        return (Number) data.get(column);
    }

    public void keepOnly(List<String> columns) {
        Set<String> fields = new HashSet<>();
        fields.addAll(data.keySet());
        Set<String> keep = new HashSet<>();
        keep.addAll(columns);
        for(String field : fields){
            if(!keep.contains(field)){
                data.removeField(field);
            }
        }
    }

    public void removeAtrribute(String name) {
        data.removeField(name);
    }

    public void removeAtrributes(List<String> columns) {
        for(String column : columns)
            data.removeField(column);
    }

    public Set<String> getFieldNames() {
        return data.keySet();
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
        Object value = data.get(oldName);
        data.removeField(oldName);
        data.put(newName, value);
    }

    public Object getGenericAttribute(String attribute) {
        return data.get(attribute);
    }

    public void setAttribute(String name, Object tupleValue) {
        data.put(name, tupleValue);
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

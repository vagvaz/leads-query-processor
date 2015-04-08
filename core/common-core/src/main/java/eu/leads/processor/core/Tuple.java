package eu.leads.processor.core;


import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by angelos on 14/3/15.
 */
public class Tuple extends DataTypeAvro implements Serializable{
    public Tuple(String jsonString) {
        super();
        JsonObject job = new JsonObject(jsonString);
        Set<String> jobstr = job.getFieldNames();

        Schema schema_new = Schema.createRecord("arrayFoo","add","mytest",false);
        List<Schema.Field> newFieldList = new ArrayList<>();

        for(String jstr: jobstr){
            newFieldList.add(new Schema.Field(jstr, Schema.create(Schema.Type.STRING), null, null));
        }

        schema_new.setFields(newFieldList);
        this.schema = schema_new;
        this.AvroRec = new GenericData.Record(this.schema);

//        System.out.println("Schema fields: "+schema.getFields());

        for(Schema.Field field: schema.getFields()){
            this.AvroRec.put(field.name(), job.getField(field.name()).toString());
        }
//        System.out.println("fields of json: " + jobstr);
    }

    public Tuple() {
        super();
        this.AvroRec = new GenericData.Record(this.schema);
    }
    public Tuple(Tuple tuple) {
        super();
        this.copy(tuple.AvroRec);
    }
    public Tuple(Tuple tl, Tuple tr, ArrayList<String> ignoreColumns) {
        super(tl.AvroRec);
        if(ignoreColumns != null) {
            tr.removeAtrributes(ignoreColumns);
        }
        tl.copy(tr.AvroRec);
    }
    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
        // Serialize it.
        //get bytes
        byte[] schemaBytes = schema.toString().getBytes();
        //write size of schema
        out.writeInt(schemaBytes.length);
        //write schema
        out.write(schemaBytes);
        // write GenericData.Record
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(out, null);
        GenericDatumWriter<Object> writer = new GenericDatumWriter<>(schema);
        writer.write(AvroRec, encoder);
        encoder.flush();
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Deserialize it.
        // rebuild Schema
        int schemaSize = in.readInt();
        byte[] schemaBytes = new byte[schemaSize];
        in.readFully(schemaBytes);
        String schemaJson = new String(schemaBytes);
        Schema schem_a = new Schema.Parser().parse(schemaJson);
        schema = schem_a;
        // rebuild GenericData.Record
        DatumReader<Object> reader = new GenericDatumReader<>(schema);
        Decoder decoder = DecoderFactory.get().directBinaryDecoder(in, null);
        AvroRec = new GenericData.Record(schema);
        reader.read(AvroRec, decoder);
    }
    private void readObjectNoData() throws ObjectStreamException{
    }
    public String asString() {
        return this.asJsonObject().toString();
//        return AvroRec.toString();
    }
    @Override
    public String toString() {
        return this.asJsonObject().toString();
//        return AvroRec.toString();
    }
    public void setAttribute(String attributeName, String value) {
        if(hasField(attributeName)) {
            AvroRec.put(attributeName, value);
        }else {
            Schema schema_new = Schema.createRecord("arrayFoo","add","mytest",false);
            List<Schema.Field> newFieldList = new ArrayList<>();
            for(Schema.Field field: schema.getFields()){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
            newFieldList.add(new Schema.Field(attributeName,schema_new,"add",null));
            schema_new.setFields(newFieldList);
            GenericRecord AvroRec_old;
            AvroRec_old = AvroRec;
            this.schema = schema_new;
            AvroRec = new GenericData.Record(this.schema);
            for(Schema.Field field: schema.getFields()){
                if(field.name().equals(attributeName)){
                    AvroRec.put(field.name(), value);
                }else{
                    AvroRec.put(field.name(), AvroRec_old.get(field.name()));
                }
            }
        }
    }
    public void setAttribute(String name, Object tupleValue) {
        if(hasField(name)) {
            AvroRec.put(name, tupleValue);
        }else {
            Schema schema_new = Schema.createRecord("arrayFoo","add","mytest",false);
            List<Schema.Field> newFieldList = new ArrayList<>();
            for(Schema.Field field: schema.getFields()){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
            newFieldList.add(new Schema.Field(name,schema_new,"add",null));
            schema_new.setFields(newFieldList);
            GenericRecord AvroRec_old;
            AvroRec_old = AvroRec;
            this.schema = schema_new;
            AvroRec = new GenericData.Record(this.schema);
            for(Schema.Field field: schema.getFields()){
                if(field.name().equals(name)){
                    AvroRec.put(field.name(), tupleValue);
                }else{
                    AvroRec.put(field.name(), AvroRec_old.get(field.name()));
                }
            }
        }
    }
    public void setNumberAttribute(String attributeName, Number value){
        if(hasField(attributeName)) {
            AvroRec.put(attributeName, value);
        }else {
            Schema schema_new = Schema.createRecord("arrayFoo","add","mytest",false);
            List<Schema.Field> newFieldList = new ArrayList<>();
            for(Schema.Field field: schema.getFields()){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
            newFieldList.add(new Schema.Field(attributeName,schema_new,"add",null));
            schema_new.setFields(newFieldList);
            GenericRecord AvroRec_old;
            AvroRec_old = AvroRec;
            this.schema = schema_new;
            AvroRec = new GenericData.Record(this.schema);
            for(Schema.Field field: schema.getFields()){
                if(field.name().equals(attributeName)){
                    AvroRec.put(field.name(), value);
                }else{
                    AvroRec.put(field.name(), AvroRec_old.get(field.name()));
                }
            }
        }
    }
    public String getValue(String column) {
        return AvroRec.get(column).toString();
    }
    public String getAttribute(String column) {
        return AvroRec.get(column).toString();
    }
    public Number getNumberAttribute(String column){
        return (Number) AvroRec.get(column);
    }
    public void keepOnly(List<String> columns) {
        Schema schema_new = Schema.createRecord("leadsrec","leadsdoc","eu.leads.processor.core",false);
        List<Schema.Field> newFieldList = new ArrayList<>();
        for(Schema.Field field: schema.getFields()){
            if(columns.contains(field.name())){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
        }
        schema_new.setFields(newFieldList);
        GenericRecord AvroRec_old;
        AvroRec_old = AvroRec;
        this.schema = schema_new;
        AvroRec = new GenericData.Record(this.schema);
        for(Schema.Field field: schema.getFields()){
            AvroRec.put(field.name(), AvroRec_old.get(field.name()));
        }
    }
    public void removeAttribute(String name) {
        Schema schema_new = Schema.createRecord("arrayFoo","test","mytest",false);
        List<Schema.Field> newFieldList = new ArrayList<>();
        for(Schema.Field field: schema.getFields()){
            if(!field.name().equals(name)){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
        }
        schema_new.setFields(newFieldList);
        GenericRecord AvroRec_old;
        AvroRec_old = AvroRec;
        this.schema = schema_new;
        AvroRec = new GenericData.Record(this.schema);
        for(Schema.Field field: schema.getFields()){
            AvroRec.put(field.name(), AvroRec_old.get(field.name()));
        }
    }
    public void removeAtrributes(List<String> columns) {
        Schema schema_new = Schema.createRecord("leadsrec","leadsdoc","eu.leads.processor.core",false);
        List<Schema.Field> newFieldList = new ArrayList<>();
        for(Schema.Field field: schema.getFields()){
            if(!columns.contains(field.name())){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }
        }
        schema_new.setFields(newFieldList);
        GenericRecord AvroRec_old;
        AvroRec_old = AvroRec;
        this.schema = schema_new;
        AvroRec = new GenericData.Record(this.schema);
        for(Schema.Field field: schema.getFields()){
            AvroRec.put(field.name(), AvroRec_old.get(field.name()));
        }
    }
    public List<String> getFieldNames() {
        List<String> fieldList = new ArrayList<>();
        for(Schema.Field fld : schema.getFields()){
            fieldList.add(fld.name());
        }
        return fieldList;
    }
    public Set<String> getFieldSet() {
        Set<String> fieldList = new HashSet<>();
        for(Schema.Field fld : schema.getFields()){
            fieldList.add(fld.name());
        }
        return fieldList;
    }
    public boolean hasField(String attribute) {
        for(Schema.Field fld : schema.getFields()){
            if(fld.name().equals(attribute)){
                return true;
            }
        }
        return false;
    }
    public void renameAttribute(String oldName, String newName) {
        if(oldName == newName)
            return;
        Schema schema_new = Schema.createRecord("arrayFoo", "test", "mytest", false);
        List<Schema.Field> newFieldList = new ArrayList<>();
        for(Schema.Field field: schema.getFields()){
            if(!field.name().equals(oldName)){
                newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
            }else{
                newFieldList.add(new Schema.Field(newName,schema_new,field.doc(),field.defaultValue()));
            }
        }
        schema_new.setFields(newFieldList);
        GenericRecord AvroRec_old;
        AvroRec_old = AvroRec;
        this.schema = schema_new;
        AvroRec = new GenericData.Record(this.schema);
        for(Schema.Field field: schema.getFields()){
            if(field.name().equals(newName)){
                AvroRec.put(field.name(), AvroRec_old.get(oldName));
            }else{
                AvroRec.put(field.name(), AvroRec_old.get(field.name()));
            }
        }
    }
    public Object getGenericAttribute(String attribute) {
        return AvroRec.get(attribute);
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
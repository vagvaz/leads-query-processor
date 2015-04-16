package eu.leads.processor.core;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by angelos on 14/3/15.
 */
public abstract class DataTypeAvro {
    protected Schema schema;
    protected GenericRecord AvroRec;

    public DataTypeAvro(GenericRecord other) {
        AvroRec = other;
    }

    public DataTypeAvro() {
        schema = SchemaBuilder
                .record("leadsrec").namespace("eu.leads.processor.core")
                .fields()
                .endRecord();
        //schema = Schema.createRecord("arrayFoo","add","mytest",false);
        AvroRec = new GenericData.Record(schema);
    }

    public JsonObject asJsonObject() {
        String avroString = AvroRec.toString();
        JsonObject objectJSON = new JsonObject(avroString);
        return objectJSON;
    }

    public String asString() {
        return this.asJsonObject().toString();
    }

    public void copy(GenericRecord other) {
        Schema schema_new = Schema.createRecord("arrayFoo","add","mytest",false);
        List<Schema.Field> newFieldList = new ArrayList<>();
        for(Schema.Field field: other.getSchema().getFields()){
            newFieldList.add(new Schema.Field(field.name(),schema_new,field.doc(),field.defaultValue()));
        }
        schema_new.setFields(newFieldList);
        this.schema = schema_new;
        AvroRec = new GenericData.Record(this.schema);
        for(Schema.Field field: other.getSchema().getFields()){
            AvroRec.put(field.name(), other.get(field.name()));
        }
    }

    @Override
    public String toString() {
        return this.asJsonObject().toString();
    }
}
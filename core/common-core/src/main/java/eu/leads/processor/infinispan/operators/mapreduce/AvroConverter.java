package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;
import org.infinispan.query.remote.avro.GenericRecordExternalizer;
import org.infinispan.query.remote.client.avro.AvroMarshaller;
import org.vertx.java.core.json.JsonObject;
//import org.apache.nutch.storage.WebPage;
import java.io.IOException;

/**
 * Created by vagvaz on 10/9/14.
 */
public class AvroConverter {
    public static Tuple getTuple(byte[] bytes, JsonObject inputSchema) {
//        AvroMarshaller<> externalizer = new GenericRecordExternalizer();
//        Tuple result = new Tuple();
//        try {
//            GenericData.Record record  = (GenericData.Record)externalizer.objectFromByteBuffer(bytes);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return result;
        return null;
    }


}

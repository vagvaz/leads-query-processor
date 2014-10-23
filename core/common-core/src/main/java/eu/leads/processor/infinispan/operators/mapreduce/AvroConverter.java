package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.infinispan.query.remote.avro.GenericRecordExternalizer;
import org.infinispan.query.remote.client.avro.AvroMarshaller;
import org.vertx.java.core.json.JsonObject;
//import org.apache.nutch.storage.WebPage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by vagvaz on 10/9/14.
 */
public class AvroConverter {
    public static Tuple getTuple(byte[] bytes, JsonObject inputSchema) {
        GenericRecordExternalizer externalizer = new GenericRecordExternalizer();
        Tuple result = new Tuple();
        try {
            GenericData.Record record  = (GenericData.Record)externalizer.objectFromByteBuffer(bytes);
            String  baseUrl = (String) record.get("baseUrl");
            int status = (int) record.get("status");
            long fetchTime = (long)record.get("fetchTime");
            long prevFetchTime = (long)record.get("prevFechTime");
            int retriesSinceFetch = (int)record.get("retriesSinceFetch");
            GenericRecord protocolStatusRecord = (GenericRecord) record.get("protocolStatus");
            ByteBuffer content = (ByteBuffer) record.get("content");
            String contentType = (String) record.get("contentType");
            ByteBuffer prevSignature = (ByteBuffer) record.get("prevSignature");
            ByteBuffer signature  = (ByteBuffer) record.get("signature");
            String title = (String)record.get("title");
            String text = (String)record.get("text");
            GenericRecord parseStatusRecord = (GenericRecord)record.get("parseStatus");
            float score = (float)record.get("score");
            String reprUrl = (String)record.get("reprUrl");
            Map<String,String> headers = (Map<String,String>)record.get("headers");
            Map<String,String> outlinks = (Map<String,String>)record.get("outlinks");
            Map<String,String> inlinks = (Map<String,String>)record.get("inlinks");
            Map<String,String> markers = (Map<String,String>)record.get("markers");
            Map<String,ByteBuffer> metadata = (Map<String, ByteBuffer>) record.get("metadata");
            String batchId = (String) record.get("batchId");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;

    }


}

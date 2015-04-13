package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 4/11/15.
 */
@Listener(clustered = false, sync = false, primaryOnly = true)
public class NutchLocalListener {
   private InfinispanManager manager;
   private String outputCacheName;
   private Cache outputCache;
   private NutchTransformer transformer;
   private String prefix = "";
   private String webpageFileName = "default.webpages";
   private String nutchOutputFileName = "nutchWebBackup";
   private String component;
   private  long globalCounter = 0;
   private long tupleCounter = 0;
   private long nutchCounter = 0;
   private ObjectOutputStream tupleDataWriter;
   private ObjectOutputStream tupleKeysWriter;
   private ObjectOutputStream nutchKeysWriter;
   private ObjectOutputStream nutchDataWriter;

   private FileOutputStream tupleOutputData =null;
   private FileOutputStream tupleOutputKeys = null;
   private FileOutputStream nutchData = null;
   private FileOutputStream nutchKeys = null;

   private static Logger log = LoggerFactory.getLogger(NutchLocalListener.class);
   private long nextRoll = 900;

   public NutchLocalListener(InfinispanManager manager, String outputCacheName,String outputPrefix,String component){
      this.manager = manager;
      this.outputCacheName = outputCacheName;
      this.component = component;
      this.prefix = outputPrefix;

      outputCache = (Cache) this.manager.getPersisentCache(outputCacheName);
      List<String> mappings = LQPConfiguration.getInstance().getConfiguration().getList("nutch.mappings");
      Map<String,String> nutchToLQE = new HashMap<String,String>();

      for(String mapping : mappings ){
         String[] keyValue = mapping.split(":");
         nutchToLQE.put(keyValue[0].trim(),keyValue[1].trim());
      }
      transformer = new NutchTransformer(nutchToLQE);
      rolloverFiles();
   }

   public void outputToFile(String key, Tuple tuple){
      try {
         tupleKeysWriter.writeUTF(key + "\n");
         JsonObject object = new JsonObject(tuple.toString());
         tupleDataWriter.writeUTF(object.encode() + "\n");
      } catch (IOException e) {
         log.error("Exception " + e.getClass().toString() + " " + e.getMessage());
      }

   }

   public void outputToFile(String key, GenericData.Record page){
      try{
         nutchKeysWriter.writeUTF(key + "\n");
         byte[] schemaBytes  = page.getSchema().toString().getBytes();

         nutchDataWriter.writeInt(schemaBytes.length);
         nutchDataWriter.write(schemaBytes);
         Encoder encoder = EncoderFactory.get().directBinaryEncoder(nutchDataWriter,null);
         GenericDatumWriter<Object> writer = new GenericDatumWriter<>(page.getSchema());
         writer.write(page, encoder);
         encoder.flush();

      }catch(IOException e){
         log.error("Exception " + e.getClass().toString() + " " + e.getMessage());
      }
   }

   public void rolloverFiles(){
      try {
         if(tupleOutputData == null || tupleOutputKeys == null || nutchData == null || nutchKeys == null) {

            File file = getOrCreate(prefix + "/" + component+"-"+ webpageFileName + "-" + tupleCounter + ".data");

            tupleOutputData = new FileOutputStream(file);
            tupleDataWriter = new ObjectOutputStream(  (tupleOutputData));
            file = getOrCreate(prefix + "/" + component + "-"+ webpageFileName + "-" + tupleCounter + ".keys");
            tupleOutputKeys = new FileOutputStream(file);
            tupleKeysWriter =new ObjectOutputStream((tupleOutputKeys));
            file = getOrCreate(prefix + "/" + component + "-"+ nutchKeys + "-" + nutchCounter + ".keys");
            nutchKeys = new FileOutputStream(file);
            nutchKeysWriter = new ObjectOutputStream( (nutchKeys));
            file = getOrCreate(prefix + "/" + component + "-"+ nutchData + "-" + nutchCounter + ".data");
            nutchData = new FileOutputStream(file);
            nutchDataWriter = new ObjectOutputStream( (nutchData));

         }
         else{
            tupleCounter++;
            nutchCounter++;
            File file = getOrCreate(prefix + "/" + component + "-"+ webpageFileName + "-" + tupleCounter + ".data");
            tupleDataWriter.close();
            tupleOutputData = new FileOutputStream(file);
            tupleDataWriter = new ObjectOutputStream( (tupleOutputData));
            file = getOrCreate(prefix + "/" + component + "-"+ webpageFileName + "-" + tupleCounter + ".keys");
            tupleKeysWriter.close();
            tupleOutputKeys = new FileOutputStream(file);
            tupleKeysWriter =new ObjectOutputStream( (tupleOutputKeys));
            file = getOrCreate(prefix + "/" + component + "-"+ nutchKeys + "-" + nutchCounter + ".keys");
            nutchKeysWriter.close();
            nutchKeys = new FileOutputStream(file);
            nutchKeysWriter = new ObjectOutputStream( (nutchKeys));
            file = getOrCreate(prefix + "/" + component + "-"+ nutchData + "-" + nutchCounter + ".data");
            nutchDataWriter.close();
            nutchData = new FileOutputStream(file);
            nutchDataWriter = new ObjectOutputStream( (nutchData));

         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private File getOrCreate(String s) {
      File result = new File(s);
      if(result.exists())
      {
         result.delete();
         try {

            result.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      else{
         try {
            result.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return  result;
   }

   @CacheEntryCreated
   public void created(CacheEntryCreatedEvent event){
      if(event.isPre())
         return;
      processWebPage(event.getKey(),event.getValue());
   }

   private void processWebPage(Object key, Object value) {


      GenericData.Record page = (GenericData.Record) value;
      System.err.println("LIstener  " + key.toString());

      if(page!=null) {
         Tuple object = transformer.transform(page);
//         BasicBSONEncoder encoder = new BasicBSONEncoder();
//         byte[] array = encoder.encode(object);
         //output to outputCache Tuple repr
         outputCache.put(outputCacheName+":"+object.getAttribute("url"),object);
         System.err.println("outputting to " + outputCacheName + "tuple " + object.toString());

         globalCounter  = (globalCounter+1) % Long.MAX_VALUE-1;
         if(globalCounter % nextRoll == 0){
            rolloverFiles();
         }
         //output to outputFile for our repr
         outputToFile(outputCacheName+":"+object.getAttribute("url"),object);

         outputToFile(key.toString(), page);

      }
   }

   @CacheEntryModified
   public void modified(CacheEntryModifiedEvent<Object,Object> event){
      if(event.isPre())
         return;
      processWebPage(event.getKey(),event.getValue());
   }
}

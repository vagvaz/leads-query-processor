import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.storage.StorageUtils;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.util.NutchConfiguration;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.Site;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 4/20/15.
 */
public class DataRemoteReader {
  NutchTransformer transformer;
  DataStore store;
  public DataRemoteReader(String connectionString){

    LQPConfiguration.initialize();
    List<String> mappings = LQPConfiguration.getInstance().getConfiguration().getList("nutch.mappings");
    Map<String,String> nutchToLQE = new HashMap<String,String>();

    for(String mapping : mappings ){
      String[] keyValue = mapping.split(":");
      nutchToLQE.put(keyValue[0].trim(),keyValue[1].trim());
    }
    transformer = new NutchTransformer(nutchToLQE);
    Configuration configuration = NutchConfiguration.create();
    configuration.set("gora.datastore.connectionstring",connectionString);
    configuration.set("gora.datastore.default","org.apache.gora.infinipan.store.InfinispanStoreer");
    try {
      store = StorageUtils.createStore(                                                               configuration, String.class, WebPage.class);
      store.createSchema();

//      query.setLimit(100);
//      query.setOffset(0);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public long storeToRemoteCache(String ensembleString,boolean distributed, long delay ) {
    EnsembleCacheManager manager = new EnsembleCacheManager(ensembleString);
    EnsembleCache cache = null;
    if (distributed) {
      cache = manager.getCache("default.webpages", new ArrayList<Site>(manager.sites()),
              EnsembleCacheManager.Consistency.DIST);
    } else {
      cache = manager.getCache("default.webpages");
    }

    Query query = store.newQuery();
//    query.setLimit(1);
    query.setOffset(0);
//    query.setFields("content");
    Result<String, WebPage> result = query.execute();
    long counter = 0;

    try {
      while (result.next()) {
        WebPage page = result.get();
        GenericData.Record record = new GenericData.Record(page.getSchema());
        for (int i = 0; i < page.getFieldsCount(); i++) {
          record.put(i, page.get(i));

        }
        Tuple tuple = transformer.transform(record);
        cache.put(cache.getName() + ":" + tuple.getAttribute("url"), tuple);
        System.out.println("t " + tuple.getAttribute("url"));
        Thread.sleep(delay);
        counter++;
        if (counter % 100 == 0) {
          System.out.println("Loaded " + counter + " tuples");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return counter;
  }

  public long storeToFile(String path, String baseName){
    int  counter = 0;int small_count=0;
    FileOutputStream nutchData = null;
    FileOutputStream nutchKeys = null;
    ObjectOutputStream nutchKeysWriter = null;
    ObjectOutputStream nutchDataWriter = null;
    File file = getOrCreate(path + "/" + baseName + "-"+ "nutchWebBackup-0.keys");

    try {
      nutchKeys = new FileOutputStream(file);
      nutchKeysWriter = new ObjectOutputStream((nutchKeys));
      file = getOrCreate(path + "/" + baseName + "-"+ "nutchWebBackup-0.data");
      nutchData = new FileOutputStream(file);
      nutchDataWriter = new ObjectOutputStream( (nutchData));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    store.createSchema();
    Query query = store.newQuery();

    int batchRead=100;
    try {
      Result<String,WebPage> result ;
      query.setLimit(batchRead);
      do {
        small_count= 0;
        query.setOffset(counter);
        result = query.execute();
        while (result.next()) {
          WebPage page = result.get();
          GenericData.Record record = new GenericData.Record(page.getSchema());
          for (int i = 0; i < page.getFieldsCount(); i++) {
//          if(page.get(i)!= null)
            {
              record.put(i, page.get(i));
            }
//          for(Schema.Field f : page.getUnmanagedFields()){
//            record.put(f.pos(),f.defaultValue());
//          }

          }
          if (record.get("content") != null)
            System.out.println("content not null");

          outputToFile(record.get(0).toString().getBytes(), record, nutchKeysWriter, nutchDataWriter);
          small_count++;
          if ((counter+small_count) % batchRead == 0)
            System.out.println("Stored " + counter + " tuples into files");

        }
        counter+=small_count;

      }while(small_count>=batchRead);
      System.out.println("Totally Stored " + counter + " tuples into files");

    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      nutchDataWriter.close();
      nutchKeysWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return counter;
  }

  public void outputToFile(byte[] key, GenericData.Record page, ObjectOutputStream nutchKeysWriter,
                           ObjectOutputStream nutchDataWriter) {
    try {
      nutchKeysWriter.writeInt(key.length);
      nutchKeysWriter.write(key);
      byte[] schemaBytes = page.getSchema().toString().getBytes();

      nutchDataWriter.writeInt(schemaBytes.length);
      nutchDataWriter.write(schemaBytes);
      Encoder encoder = EncoderFactory.get().directBinaryEncoder(nutchDataWriter, null);
      GenericDatumWriter<Object> writer = new GenericDatumWriter<>(WebPage.SCHEMA$);
      writer.write(page, encoder);
      encoder.flush();

    } catch (IOException e) {
      System.err.println("Exception " + e.getClass().toString() + " " + e.getMessage());
    }
  }


  private File getOrCreate(String s) {
    File result = new File(s);
    if(result.exists())
    {
      result.delete();
      try {
        result.getParentFile().mkdir();
        result.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else{
      try {
        result.getParentFile().mkdir();
        result.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return  result;
  }
}

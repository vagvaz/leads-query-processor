import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.nutch.storage.WebPage;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.query.remote.client.avro.AvroMarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.avro.generic.GenericData.*;

/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTool {
   private String baseDir;
   private String webpagePrefix;
   private String nutchDataPrefix;
   private String ensembleString;
   private EnsembleCacheManager emanager;
   private EnsembleCache  nutchCache;
   private EnsembleCache webpageCache;
   private NutchTransformer nutchTransformer;
   public ReplayTool(String baseDir, String webpagePrefix, String nutchDataPrefix, String ensembleString){
      this.baseDir = baseDir;
      this.webpagePrefix = webpagePrefix;
      this.nutchDataPrefix = nutchDataPrefix;
      this.ensembleString = ensembleString;
      LQPConfiguration.initialize();
      emanager = new EnsembleCacheManager((ensembleString), new AvroMarshaller<>(WebPage.class));
      nutchCache = emanager.getCache("WebPage");
      webpageCache = emanager.getCache("default.webpages");
      List<String> mappings = LQPConfiguration.getInstance().getConfiguration().getList("nutch.mappings");
      Map<String,String> nutchToLQE = new HashMap<String,String>();

      for(String mapping : mappings ){
         String[] keyValue = mapping.split(":");
         nutchToLQE.put(keyValue[0].trim(),keyValue[1].trim());
      }
      nutchTransformer = new NutchTransformer(nutchToLQE);
   }

   public void replayNutch(boolean load){
      int currentCounter = 0;
      while(true){
         try{
            File keyFile = new File(baseDir+"/"+nutchDataPrefix+"-"+currentCounter+".keys");
            File dataFile = new File(baseDir+"/"+nutchDataPrefix+"-"+currentCounter+".data");
            if(keyFile.exists()) {
               ObjectInputStream keyFileIS = new ObjectInputStream(new FileInputStream(keyFile));
               ObjectInputStream dataFileIS = new ObjectInputStream(new FileInputStream(dataFile));
               while (keyFileIS.available() > 0){
                  int keysize = keyFileIS.readInt();
                  byte[] key = new byte[keysize];
                  keyFileIS.readFully(key);
                  int schemaSize = dataFileIS.readInt();
                  byte[] schemaBytes = new byte[schemaSize];
                  dataFileIS.readFully(schemaBytes);
                  String schemaJson = new String(schemaBytes);
                  Schema schema = new Schema.Parser().parse(schemaJson);

                  // rebuild GenericData.Record
                  DatumReader<Object> reader = new GenericDatumReader<>(schema);
                  Decoder decoder = DecoderFactory.get().directBinaryDecoder(dataFileIS, null);
                  GenericData.Record page = new GenericData.Record(schema);
                  reader.read(page, decoder);
                  System.err.println("Read key: " + new String(key) + "\n" + "value " + page.toString());

                  if(load)
                  {
                     Tuple t  = nutchTransformer.transform(page);
                     webpageCache.put(webpageCache.getName()+":"+t.getAttribute("url"),t);
                  }
               }
               currentCounter++;
            }
            else{
               System.out.println("read " + currentCounter + " files");
               break;
            }
         }catch(Exception e ){
            e.printStackTrace();
         }
      }
   }

}

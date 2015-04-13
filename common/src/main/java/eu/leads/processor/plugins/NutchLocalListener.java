package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.apache.avro.generic.GenericData;
import org.apache.nutch.storage.WebPage;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.query.remote.client.avro.AvroMarshaller;

import java.io.IOException;

/**
 * Created by vagvaz on 4/11/15.
 */
@Listener(clustered = false, sync = false, primaryOnly = true)
public class NutchLocalListener {
   private InfinispanManager manager;
   private String outputCacheName;
   private Cache outputCache;
   NutchTransformer transformer;
   AvroMarshaller<WebPage> marshaller;

   public NutchLocalListener(InfinispanManager manager, String outputCacheName){
      this.manager = manager;
      this.outputCacheName = outputCacheName;
      outputCache = (Cache) this.manager.getPersisentCache(outputCacheName);
      transformer = new NutchTransformer();
      marshaller = new AvroMarshaller<>(WebPage.class);
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
         BasicBSONObject object = transformer.transform(page);
//         BasicBSONEncoder encoder = new BasicBSONEncoder();
//         byte[] array = encoder.encode(object);
         outputCache.put(outputCacheName+":"+object.get("url"),object.toString());
         System.err.println("outputting to " + outputCacheName + "tuple " + object.toString());
      }
   }

   @CacheEntryModified
   public void modified(CacheEntryModifiedEvent<Object,Object> event){
      if(event.isPre())
         return;
      processWebPage(event.getKey(),event.getValue());
   }
}

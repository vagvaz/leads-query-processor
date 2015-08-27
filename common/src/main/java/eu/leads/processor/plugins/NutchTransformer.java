package eu.leads.processor.plugins;

import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.nutch.storage.WebPage;

/**
 * Created by vagvaz on 4/11/15.
 */
public class NutchTransformer {
   Map<String,String> webpageMapping;
   public NutchTransformer(Map<String,String> mapping)
   {
      webpageMapping = new HashMap<>();
      webpageMapping.putAll(mapping);

   }



   public Tuple transform(GenericData.Record wp) {
      Tuple tuple =  new Tuple();

      for(Map.Entry<String,String> entry : webpageMapping.entrySet()){
         if(wp.get(entry.getKey()) == null){
            tuple.setAttribute("default.keywords."+entry.getValue(),null);
            continue;
         }
         if(entry.getValue().equals("links")){
            List<String> links = new ArrayList<>();
            Map<String,String> mapLinks = ((Map<String,String>)wp.get("outlinks"));
            if(mapLinks!= null) {
               for (String outlink : mapLinks.values()) {
                  links.add(outlink);
               }
            }
            tuple.setAttribute("default.keywords."+"links",links);

         }
         else if(entry.getValue().equals("body")){
            ByteBuffer byteBuffer = (ByteBuffer) wp.get(entry.getKey());
            if(byteBuffer != null)
               tuple.setAttribute("default.keywords."+"body",new String(byteBuffer.array () ));
            else{
               tuple.setAttribute("default.keywords."+"body",null);
            }
         }
         else if(entry.getValue().equals("ts")) {
//            Date  date = new Date((long)wp.get(entry.getKey()));
//            SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

            tuple.setAttribute("default.keywords."+"ts", (long)wp.get(entry.getKey()));
         }
         else if(entry.getValue().equals("url")) {
            tuple.setAttribute("default.keywords."+"url", wp.get(entry.getValue()));
         }
         else{
            tuple.setAttribute("default.keywords."+entry.getValue(),wp.get(entry.getKey()));
         }

      }
      tuple.setAttribute("default.keywords."+"pagerank",-1.0);
      tuple.setAttribute("default.keywords."+"sentiment",-1.0);
      tuple.setAttribute("default.keywords."+"responseCode",200);
//
//      tuple.setAttribute("default.keywords."+"url", wp.get("key"));
//      tuple.setAttribute("default.keywords."+"body", wp.get("content"));
//      tuple.setAttribute("default.keywords."+"headers", wp.get("headers"));
//      tuple.setAttribute("default.keywords."+"responseTime", wp.get("fetchInterval"));
//      tuple.setAttribute("default.keywords."+"responseCode", 200);
//      tuple.setAttribute("default.keywords."+"charset", wp.get("contentType"));
//      tuple.setAttribute("default.keywords."+"links", Arrays.asList(((Map<String, String>) wp.get("outlinks")).keySet()));
//      tuple.setAttribute("default.keywords."+"title", wp.get("title"));
//      tuple.setAttribute("default.keywords."+"pagerank", -1.0);
//      long ft = (long) wp.get("fetchTime");
//      Date  date = new Date(ft);
//      SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
//      tuple.setAttribute("default.keywords."+"published", df2.format(date));
//      tuple.setAttribute("default.keywords."+"sentiment", -1.0);
      return tuple;
   }
}

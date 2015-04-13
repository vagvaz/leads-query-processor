package eu.leads.processor.plugins;

import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;
import org.apache.nutch.storage.WebPage;
import org.bson.BasicBSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

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
         if(entry.getValue().equals("links")){
            List<String> links = new ArrayList<>();
            for(String outlink : ((Map<String,String>)wp.get("outlinks")).values()){
               links.add(outlink);
            }
            tuple.setAttribute("links",links);

         }
         else if(entry.getValue().equals("body")){
            ByteBuffer byteBuffer = (ByteBuffer) wp.get(entry.getKey());
            if(byteBuffer != null)
            tuple.setAttribute("body",new String(byteBuffer.toString() ));
            else{
               tuple.setAttribute("body",null);
            }
         }
         else if(entry.getValue().equals("published")) {
            Date  date = new Date((long)wp.get(entry.getKey()));
            SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            tuple.setAttribute("published", df2.format(date));
         }
         else{
            tuple.setAttribute(entry.getValue(),wp.get(entry.getKey()));
         }
         tuple.setAttribute("pagerank",-1.0);
         tuple.setAttribute("sentiment",-1.0);
         tuple.setAttribute("responseCode",200);
      }
//
//      tuple.setAttribute("url", wp.get("key"));
//      tuple.setAttribute("body", wp.get("content"));
//      tuple.setAttribute("headers", wp.get("headers"));
//      tuple.setAttribute("responseTime", wp.get("fetchInterval"));
//      tuple.setAttribute("responseCode", 200);
//      tuple.setAttribute("charset", wp.get("contentType"));
//      tuple.setAttribute("links", Arrays.asList(((Map<String, String>) wp.get("outlinks")).keySet()));
//      tuple.setAttribute("title", wp.get("title"));
//      tuple.setAttribute("pagerank", -1.0);
//      long ft = (long) wp.get("fetchTime");
//      Date  date = new Date(ft);
//      SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
//      tuple.setAttribute("published", df2.format(date));
//      tuple.setAttribute("sentiment", -1.0);
      return tuple;
   }
}

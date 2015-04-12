package eu.leads.processor.plugins;

import org.apache.avro.generic.GenericData;
import org.apache.nutch.storage.WebPage;
import org.bson.BasicBSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by vagvaz on 4/11/15.
 */
public class NutchTransformer {
   
   public BasicBSONObject transform(GenericData.Record wp) {
      BasicBSONObject tuple =  new BasicBSONObject();
      
      tuple.put("url", wp.get("baseUrl"));
      tuple.put("body", wp.get("content"));
      tuple.put("headers", wp.get("headers"));
      tuple.put("responseTime", wp.get("fetchInterval"));
      tuple.put("responseCode", 200);
      tuple.put("charset", wp.get("contentType"));
      tuple.put("links", Arrays.asList(((Map<String, String>) wp.get("outlinks")).keySet()));
      tuple.put("title", wp.get("title"));
      tuple.put("pagerank", -1.0);
      long ft = (long) wp.get("fetchTime");
      Date  date = new Date(ft);
      SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
      tuple.put("published", df2.format(date));
      tuple.put("sentiment", -1.0);
      return tuple;
   }
}

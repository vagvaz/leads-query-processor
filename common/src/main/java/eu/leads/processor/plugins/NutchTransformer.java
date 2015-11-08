package eu.leads.processor.plugins;

import eu.leads.processor.common.google.pagerank.Pagerank;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.apache.avro.generic.GenericData;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

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
         if(wp.get(entry.getKey()) == null && !(entry.getValue().toLowerCase().equals("domainname") || entry.getValue().equals("pagerank"))){
            tuple.setAttribute("default.webpages."+entry.getValue(),null);
            continue;
         }
         if(entry.getValue().equals("links")){
            List<String> links = new ArrayList<>();
            Map<String,String> mapLinks = ((Map<String,String>)wp.get("outlinks"));
            if(mapLinks!= null) {
               for (String outlink : mapLinks.keySet()) {
                  links.add(outlink);
               }
            }
            tuple.setAttribute("default.webpages."+"links",links);

         }
         else if(entry.getValue().equals("body")){
            ByteBuffer byteBuffer = (ByteBuffer) wp.get(entry.getKey());
            if(byteBuffer != null)
               tuple.setAttribute("default.webpages."+"body",new String(byteBuffer.array () ));
            else{
               tuple.setAttribute("default.webpages."+"body",null);
            }
         }
         else if(entry.getValue().equals("ts")) {
//            Date  date = new Date((long)wp.get(entry.getKey()));
//            SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

            tuple.setAttribute("default.webpages."+"ts", (long)wp.get(entry.getKey()));
         }
         else if(entry.getValue().equals("url")) {
//            tuple.setAttribute("default.webpages."+"url", wp.get(entry.getKey()));
            String url = (String) wp.get("url");
            if(url.startsWith("http") && LQPConfiguration.getInstance().getConfiguration().getBoolean("use.nutch.url",false)){
               url = transformUri(url);
            }
            tuple.setAttribute("default.webpages."+"url", url);

         }
         else if (entry.getValue().equals("domainName")){
            String transforUri = transformUri((String) wp.get("url"));
            try {
               tuple.setAttribute("default.webpages.domainName",Pagerank.getDomainName(transforUri));
            } catch (URISyntaxException e) {
               e.printStackTrace();
            }
         }
         else if (entry.getValue().equals("pagerank")){
            try {
               String transforUri = transformUri((String) wp.get("url"));
               double pagerank = Pagerank.get((String) wp.get("url"));
               tuple.setAttribute("default.webpages.pagerank", pagerank);
//               Random random  = new Random(wp.get("url").hashCode());
//               double d = random.nextDouble();
//               tuple.setAttribute("default.webpages.pagerank",(Math.ceil(100*d))/10.0);
            }
            catch (Exception e){
               Random random  = new Random(wp.get("url").hashCode());
               double d = random.nextDouble();
               tuple.setAttribute("default.webpages.pagerank",(Math.ceil(100*d))/10.0);
            }
         }
         else{
            tuple.setAttribute("default.webpages."+entry.getValue(),wp.get(entry.getKey()));
         }

      }
//      tuple.setAttribute("default.webpages."+"pagerank",-1.0);
      tuple.setAttribute("default.webpages."+"sentiment",0.0);
      tuple.setAttribute("default.webpages."+"responseCode",200);
//
//      tuple.setAttribute("default.webpages."+"url", wp.get("key"));
//      tuple.setAttribute("default.webpages."+"body", wp.get("content"));
//      tuple.setAttribute("default.webpages."+"headers", wp.get("headers"));
//      tuple.setAttribute("default.webpages."+"responseTime", wp.get("fetchInterval"));
//      tuple.setAttribute("default.webpages."+"responseCode", 200);
//      tuple.setAttribute("default.webpages."+"charset", wp.get("contentType"));
//      tuple.setAttribute("default.webpages."+"links", Arrays.asList(((Map<String, String>) wp.get("outlinks")).keySet()));
//      tuple.setAttribute("default.webpages."+"title", wp.get("title"));
//      tuple.setAttribute("default.webpages."+"pagerank", -1.0);
//      long ft = (long) wp.get("fetchTime");
//      Date  date = new Date(ft);
//      SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
//      tuple.setAttribute("default.webpages."+"published", df2.format(date));
//      tuple.setAttribute("default.webpages."+"sentiment", -1.0);
      return tuple;
   }

   private String transformUri(String standardUrl) {
      String nutchUrl = "";
      URL url_;
      try {
         url_ = new URL(standardUrl);

         String authority = url_.getAuthority();
         String protocol  = url_.getProtocol();
         String file      = url_.getFile();

         String [] authorityParts = authority.split("\\.");
         for(int i=authorityParts.length-1; i>=0; i--)
            nutchUrl += authorityParts[i] + ".";
         nutchUrl = nutchUrl.substring(0, nutchUrl.length()-1);
         nutchUrl += ":" + protocol;
         nutchUrl += file;

      } catch (MalformedURLException e) {
         e.printStackTrace();
         return null;
      }

      return nutchUrl;
   }
}

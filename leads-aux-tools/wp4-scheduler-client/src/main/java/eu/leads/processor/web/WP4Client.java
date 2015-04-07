package eu.leads.processor.web;

import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 4/4/15.
 */
public class WP4Client {
   private  static String host;
   private static  String port;


   public static void initialize(String host, String port ){
      WP4Client.host = host;
      WP4Client.port = port;
   }

   public static JsonObject evaluatePlan(JsonObject plan){
      JsonObject result = null;
      //Send data to scheduler
      // result = getResult;

      return result;
   }
}

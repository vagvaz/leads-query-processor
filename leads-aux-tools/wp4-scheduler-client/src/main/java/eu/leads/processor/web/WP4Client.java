package eu.leads.processor.web;

import org.vertx.java.core.json.JsonObject;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by vagvaz on 4/4/15.
 */
public class WP4Client {
   private  static String host;
   private static  String port;
   private static URL address;
   private static String prefix="/";

   public static void initialize(String host, String port ){
      WP4Client.host = host;
      WP4Client.port = port;
   }

   public static JsonObject evaluatePlan(JsonObject plan, String host , String port) throws IOException {
      //Send data to scheduler
      JsonObject result = null;


      address = new URL(host + ":" + port + prefix + "scheduleStages");
      HttpURLConnection connection = (HttpURLConnection) address.openConnection();
      connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
      setBody(connection, plan);
      String response = getResult(connection);
      result = new JsonObject(response);
      return result;
   }


   private static HttpURLConnection setUp(HttpURLConnection connection, String type,
                                          String contentType, boolean hasInput,
                                          boolean hasOutput) throws ProtocolException {
      connection.setRequestMethod(type);
      connection.setRequestProperty("Content-Type", contentType);
      connection.setUseCaches(false);
      connection.setDoInput(hasInput);
      connection.setDoOutput(hasOutput);
      connection.setConnectTimeout(4000);
      //connection.setReadTimeout(10000);
      return connection;
   }

   private static String getResult(HttpURLConnection connection) throws IOException {
      //        System.out.println("getResult");
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
         response.append(line);
      }
      rd.close();
      response.trimToSize();
      //        System.out.println("received: " + response);
      return response.toString();
   }


   private static void setBody(HttpURLConnection connection, JsonObject body) throws IOException {
      String output = body.toString();
      //        System.out.println("Size: " + output.getBytes().length);
      DataOutputStream os = new DataOutputStream(connection.getOutputStream());
      os.writeBytes(output);
      os.flush();
      os.close();
   }

}

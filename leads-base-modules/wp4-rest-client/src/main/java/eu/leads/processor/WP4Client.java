package eu.leads.processor;

import org.vertx.java.core.json.JsonObject;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by vagvaz on 10/23/14.
 */
public class WP4Client {
    private  static String host;
    private static String port;
    private final static String datacentersPrefix = "/dataceneters/";
    private final static String s3recourses = "/s3resoucers/";
    private final static String s3placement = "/s3resources/place/";
    private final static String scheduleVMprefix = "scheduleVM";
    private final static String scheduleStages ="scheduleStages";
    private static String prefix = "";
    private static URL address;


    public static boolean initialize(String host, int port) throws MalformedURLException {
        boolean result = false;
        WP4Client.host = host;
        WP4Client.port = String.valueOf(port);
        WP4Client.address = new URL(WP4Client.host+":"+WP4Client.port);
        prefix = WP4Client.host+":"+WP4Client.port+"/";
        result = checkIfOnline();
        return result;
    }

    private static boolean checkIfOnline() {
        HttpURLConnection connection = null;
        try {

            connection = (HttpURLConnection) address.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private static HttpURLConnection setUp(HttpURLConnection connection, String type,
                                           String contentType, boolean hasInput,
                                           boolean hasOutput) throws ProtocolException {
        connection.setRequestMethod(type);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setUseCaches(false);
        connection.setDoInput(hasInput);
        connection.setDoOutput(hasOutput);
        return connection;
    }

    private static void setBody(HttpURLConnection connection, JsonObject body) throws IOException {
        String output = body.toString();
//        System.out.println("Size: " + output.getBytes().length);
        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        os.writeBytes(output);
        os.flush();
        os.close();
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

    public static JsonObject getDataCenters(){
        JsonObject result = new JsonObject();
        try {
            address =  new URL(prefix+datacentersPrefix);
            HttpURLConnection connection = (HttpURLConnection)address.openConnection();
            connection = setUp(connection,"GET", MediaType.APPLICATION_JSON,true,true);
            String response = getResult(connection);
            result = new JsonObject(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static JsonObject getS3resources(){
        JsonObject result = new JsonObject();
        try {
            address =  new URL(prefix+s3recourses);
            HttpURLConnection connection = (HttpURLConnection)address.openConnection();
            connection = setUp(connection,"GET", MediaType.APPLICATION_JSON,true,true);
            String response = getResult(connection);
            result = new JsonObject(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonObject paceBucket(String bucketName){
        JsonObject result = new JsonObject();
        try {
            address =  new URL(prefix+s3placement+"/"+bucketName);
            HttpURLConnection connection = (HttpURLConnection)address.openConnection();
            connection = setUp(connection,"GET", MediaType.APPLICATION_JSON,true,true);
            String response = getResult(connection);
            result = new JsonObject(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonObject scheduleVM(String bucketName,float q, float k){
        JsonObject result = new JsonObject();
        try {
            address =  new URL(prefix+ scheduleVMprefix+"/"+bucketName+","+String.valueOf(q)+","+String.valueOf(k));
            HttpURLConnection connection = (HttpURLConnection)address.openConnection();
            connection = setUp(connection,"GET", MediaType.APPLICATION_JSON,true,true);
            String response = getResult(connection);
            result = new JsonObject(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonObject scheduleMultipleStages(JsonObject plan){
        JsonObject result = new JsonObject();
        try {
            address =  new URL(prefix+scheduleStages);
            HttpURLConnection connection = (HttpURLConnection)address.openConnection();
            connection = setUp(connection,"POST", MediaType.APPLICATION_JSON,true,true);
            setBody(connection,plan);
            String response = getResult(connection);
            result = new JsonObject(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

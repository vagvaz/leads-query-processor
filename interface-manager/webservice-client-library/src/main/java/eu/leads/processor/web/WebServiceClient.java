package eu.leads.processor.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.PluginPackage;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.SerializationUtils;
import org.vertx.java.core.json.JsonObject;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 8/15/14.
 */
public class WebServiceClient {
    private final static String prefix = "/rest/";
    private final static ObjectMapper mapper = new ObjectMapper();
    private static String host;
    private static String port;
    private static URL address;

    public static boolean initialize(String url, int p) throws MalformedURLException {
        host = url;
        port = String.valueOf(p);
        address = new URL(host + ":" + port);
        return checkIfOnline();
    }

    private static boolean checkIfOnline() {
        HttpURLConnection connection = null;
        try {
            address = new URL(host + ":" + port + prefix + "checkOnline");
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

    private static String getResult(HttpURLConnection connection) throws IOException {
        System.out.println("getResult");
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuffer response = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();
        response.trimToSize();
        System.out.println("received: " + response);
        return response.toString();
    }

    private static void setBody(HttpURLConnection connection, Object body) throws IOException {
        String output = mapper.writeValueAsString(body);
        System.out.println("Size: " + output.getBytes().length);
        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        os.writeBytes(output);
        os.flush();
        os.close();
    }

    private static void setDataBody(HttpURLConnection connection, byte[] data) throws IOException {
        //String output = mapper.writeValueAsString(body);
       // System.out.println("Size: " + output.getBytes().length);
        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        //byte[] data = SerializationUtils.serialize(body);
        System.out.println("setDataBody length: " + data.length);

        os.write(data,0,data.length);

        os.flush();
        os.close();
    }

    public static JsonObject getObject(String table, String key, List<String> attributes)
        throws IOException {
        ObjectQuery ob = new ObjectQuery();
        ob.setAttributes(attributes);
        ob.setKey(key);
        ob.setTable(table);
        String atr = "";
        ob.setAttributes(attributes);
        address = new URL(host + ":" + port + prefix + "object/get/");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
        setBody(connection, ob);
        String response = getResult(connection);
        //        System.out.println("getResponse " + response);
        if (response.length() < 5)
            return null;
        //      HashMap<String,String> res = (HashMap<String, String>) mapper.readValue(response, HashMap.class);
        //      HashMap<String,String> result = new HashMap<>();
        //        for(Map.Entry<String,String> r : res.entrySet()){
        ////            if(!r.getValue().startsWith("["))
        ////               result.put(r.getKey(),mapper.readValue(r.getValue(),String.class));
        ////            else
        //               result.put(r.getKey(),r.getValue());
        //        }
        JsonObject result = new JsonObject(response);
        return result;
    }

    public static boolean putObject(String table, String key, JsonObject object)
        throws IOException {
        boolean result = false;
        PutAction action = new PutAction();
        action.setTable(table);
        action.setKey(key);
        action.setObject(object.toString());
        address = new URL(host + ":" + port + prefix + "object/put/");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
        //        setBody(connection,mapper.writeValueAsString(action));
        setBody(connection, action);
        String response = getResult(connection);
        ActionResult aresult = mapper.readValue(response, ActionResult.class);
        result = aresult.getStatus().equals("SUCCESS");
        return result;
    }

    public static QueryStatus getQueryStatus(String id) throws IOException {
        QueryStatus result = new QueryStatus();
        address = new URL(host + ":" + port + prefix + "query/status/" + id);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "GET", MediaType.APPLICATION_JSON, true, true);
        String response = getResult(connection);
        System.err.println("responsed " + response);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }

    public static QueryResults getQueryResults(String id, long min, long max) throws IOException {
        QueryResults result = new QueryResults();
        address = new URL(host + ":" + port + prefix + "query/results/" + id + "/min/" + String
                                                                                             .valueOf(min)
                              + "/max/" + String.valueOf(max));
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "GET", MediaType.APPLICATION_JSON, true, true);
        String response = getResult(connection);
        result = new QueryResults(new JsonObject(response));
        return result;
    }

    public static QueryStatus submitQuery(String username, String SQL) throws IOException {
        QueryStatus result = null;
        WebServiceQuery query = new WebServiceQuery();
        query.setSql(SQL);
        query.setUser(username);
        address = new URL(host + ":" + port + prefix + "query/submit");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
        setBody(connection, query);
        String response = getResult(connection);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }

    public static QueryStatus submitWorkflow(String username, String workflow) throws IOException {
        QueryStatus result = null;
        WebServiceWorkflow query = new WebServiceWorkflow();
        query.setWorkflow(workflow);
        query.setUser(username);
        address = new URL(host + ":" + port + prefix + "workflow/submit");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
        setBody(connection, query);
        String response = getResult(connection);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }

    public static QueryStatus deployPlugin(String username, String pluginId, XMLConfiguration config, String cacheName,
                                           EventType[] events) throws IOException {
        QueryStatus result = null;
        byte[] data = SerializationUtils.serialize(config);
        JsonObject req = new JsonObject();
        req.putString("pluginid",pluginId);
        req.putString("cachename",cacheName);
        req.putString("user",username);
        req.putBinary("config",data);


        if(events == EventType.ALL)
        req.putString("eventType","ALL");
        else if(events == EventType.ALL)
        req.putString("eventType","CREATEANDMODIFY");

        address = new URL(host + ":" + port + prefix + "deploy/plugin/"+pluginId+"/"+cacheName);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.MULTIPART_FORM_DATA, true, true);

        setBody(connection, req);
        String response = getResult(connection);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }


    public static QueryResults undeployPlugin(String username, String pluginId, String cacheName) throws IOException {
        QueryResults result = new QueryResults();
        address = new URL(host + ":" + port + prefix + "undeploy/plugin/"+pluginId+"/"+cacheName);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "GET", MediaType.APPLICATION_JSON, true, true);
        String response = getResult(connection);
        result = new QueryResults(new JsonObject(response));
        return result;
    }

    public static QueryStatus submitPlugin(String username, PluginPackage pluginPackage) throws IOException {
        QueryStatus result = null;
        WebServiceWorkflow query = new WebServiceWorkflow();

        pluginPackage.putString("user",username);

        byte[] data = SerializationUtils.serialize(pluginPackage);

        address = new URL(host + ":" + port + prefix + "data/submit");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.MULTIPART_FORM_DATA, true, true);

        setDataBody(connection, data);
        String response = getResult(connection);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }

    public static QueryStatus submitData(String username, JsonObject data) throws IOException {
         QueryStatus result = null;
        data.putString("user",username);
        address = new URL(host + ":" + port + prefix + "data/submit");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.MULTIPART_FORM_DATA, true, true);

        setBody(connection, data.toString());
        String response = getResult(connection);
        result = mapper.readValue(response, QueryStatus.class);
        return result;
    }



    public static QueryStatus submitData(String username, byte[] data) throws IOException {
        QueryStatus result = null;
        WebServiceWorkflow query = new WebServiceWorkflow();

        address = new URL(host + ":" + port + prefix + "data/submit");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection = setUp(connection, "POST", MediaType.MULTIPART_FORM_DATA, true, true);

        setDataBody(connection, data);
        String response = getResult(connection);
        //result = mapper.readValue(response, QueryStatus.class);
        return null;//result;
    }

    public static JsonObject submitSpecialQuery(String username, String type,
                                                            Map<String, String> parameters)
        throws IOException {
//       Map<String,String> result = new HashMap<>();
      JsonObject result = new JsonObject();
      if (type.equals("rec_call")) {

            RecursiveCallRestQuery query = new RecursiveCallRestQuery();
            query.setUser(username);
            query.setDepth(parameters.get("depth"));
            query.setUrl(parameters.get("url"));
            address = new URL(host + ":" + port + prefix + "query/wgs/rec_call");
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            connection = setUp(connection, "POST", MediaType.APPLICATION_JSON, true, true);
            setBody(connection, query);
            String response = getResult(connection);
            JsonObject reply = new JsonObject(response);
//            result.put("id",reply.getString("id"));
//            result.put("output",reply.getString("output"));
            result = reply;
        }
        return result;
    }
}

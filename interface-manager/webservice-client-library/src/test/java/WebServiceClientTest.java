import eu.leads.processor.core.Tuple;
import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Created by vagvaz on 8/23/14.
 */
public class WebServiceClientTest {
    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException {
//        host = "http://localhost";
        host = "http://5.147.254.198";
        port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            if(WebServiceClient.initialize(host, port))
                System.out.println("Server is Up");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObject object = new JsonObject();
        object.putString("name", "vag");
        object.putString("surname", "vaz");
        object.putString("age", "18");
        object.putString("id", "91818111");
        try {
            WebServiceClient.putObject("testCache", object.getString("id"), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject mapObject =
            WebServiceClient.getObject("testCache", object.getString("id"), null);
        if (mapObject.toString().equals(object.toString())) {
            System.out.println("Equals " + object.toString() + "\n" + mapObject.toString());
        } else {
            System.out.println("PROBLEM");
            System.out.println(object.toString());
            System.out.println(mapObject.toString());
        }

        String workflow = "{\n" +
                "  \"IsDistinct\": false,\n" +
                "  \"Projections\": [\n" +
                "    {\n" +
                "      \"Expr\": {\n" +
                "        \"OpType\": \"Asterisk\"\n" +
                "      },\n" +
                "      \"OpType\": \"Target\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Expr\": {\n" +
                "    \"Relations\": [\n" +
                "      {\n" +
                "        \"TableName\": \"webpages\",\n" +
                "        \"OpType\": \"Relation\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"OpType\": \"RelationList\"\n" +
                "  },\n" +
                "  \"OpType\": \"Projection\"\n" +
                "}";
        QueryStatus  currentStatus = WebServiceClient.submitWorkflow("testUser", workflow);

        while(!currentStatus.getStatus().equals("COMPLETED") && !currentStatus.getStatus().equals("FAILED")){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentStatus = WebServiceClient.getQueryStatus(currentStatus.getId());
        }
        if(currentStatus.getStatus().equals("COMPLETED")) {
            QueryResults results = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
            System.out.println("worflow query results size " + results.getResult().size());
        }
        String sampleQuery =  " SELECT url from webpages order by url";

        currentStatus = WebServiceClient.submitQuery("webServiceTest",sampleQuery);
        while(!currentStatus.getStatus().equals("COMPLETED") && !currentStatus.getStatus().equals("FAILED")){
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          currentStatus = WebServiceClient.getQueryStatus(currentStatus.getId());
        }

        if(currentStatus.getStatus().equals("COMPLETED")){
          QueryResults results = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
          String firstUrl = results.getResult().get(0);
          results.getResult().clear();
          results = null;
          Tuple t = new Tuple(firstUrl);
          HashMap<String,String> properties = new HashMap<>();
          properties.put("url",t.getAttribute("default.webpages.url"));
          properties.put("depth","3");
          JsonObject wgsreply =  WebServiceClient.submitSpecialQuery("webServiceTest","rec_call",properties);
          QueryStatus wgsStatus = WebServiceClient.getQueryStatus(wgsreply.getString("id"));
          while(!wgsStatus.getStatus().equals("COMPLETED") && !wgsStatus.getStatus().equals("FAILED")){
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            wgsStatus = WebServiceClient.getQueryStatus(currentStatus.getId());
          }
          if(wgsStatus.getStatus().equals("COMPLETED")){
            JsonObject level0 = WebServiceClient.getObject(wgsreply.getString("output"),"0",null);
            JsonObject level1 = WebServiceClient.getObject(wgsreply.getString("output"),"1",null);
            JsonObject level2 = WebServiceClient.getObject(wgsreply.getString("output"),"2",null);
//            JsonObject level3 = WebServiceClient.getObject(wgsreply.getString("output"),"3",null);
            System.out.println("===========    0   ================= \n"+level0.encodePrettily());
            System.out.println("===========    1   ================= \n"+level1.encodePrettily());
            System.out.println("===========    2   ================= \n"+level2.encodePrettily());
//            System.out.println("===========    3   ================= \n"+level3.encodePrettily());
          }
          else{
            System.err.println("WGS Operator FAILED");
          }
        }
        else{
          System.out.println("Query FAILED so I cannot test Web Graph Service Operator");
        }


    }

}

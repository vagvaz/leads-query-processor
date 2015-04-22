package eu.leads.processor.web;

import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by angelos on 20/04/15.
 */
public class WebServiceClientTest {

    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException {
        host = "http://localhost";
        port = 8080;

        try {
            if (WebServiceClient.initialize(host, port))
                System.out.println("Server is Up");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObject object = new JsonObject();
        object.putString("name", "angelos");
        object.putString("surname", "angelidakis");
        object.putString("age", "25");
        object.putString("id", "78971234");
//        try {
//            WebServiceClient.connWebSocket("123", "456", "leadsTeam", object);
//            WebServiceClient.connWebSocket("123", "456", "leadsTeam", object);
//            WebServiceClient.monitorQueryResults(1,"angelos",2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

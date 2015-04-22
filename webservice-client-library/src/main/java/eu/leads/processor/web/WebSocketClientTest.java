package eu.leads.processor.web;

import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by angelos on 27/03/15.
 */
public class WebSocketClientTest{

    private static String host;
    private static int port;


    public static void main(String[] args) {
        try {
            // open websocket
            WebSocketClient client1 = new WebSocketClient(new URI("ws://localhost:8080/myapp"));
            WebSocketClient client2 = new WebSocketClient(new URI("ws://localhost:8080/myapp"));
            WebSocketClient client3 = new WebSocketClient(new URI("ws://localhost:8080/myapp"));
            WebSocketClient client4 = new WebSocketClient(new URI("ws://localhost:8080/myapp"));
            JsonObject object = new JsonObject();
            host = "http://localhost";
            port = 8080;

            try {

                if (client1.initialize(host, port))
                    System.out.println("client1: Server is Up");

                if (client2.initialize(host, port))
                    System.out.println("client2: Server is Up");

                if (client3.initialize(host, port))
                    System.out.println("client3: Server is Up");

                if (client4.initialize(host, port))
                    System.out.println("client4: Server is Up");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // add listener
            client1.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // add listener
            client3.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // add listener
            client3.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // add listener
            client4.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            object.putString("name", "angelos");
            object.putString("surname", "angelidakis");
            object.putString("age", "25");
            object.putString("id", "78971234");
            client1.sendMessage("123", "456", "leadsTeam", object);

            object.putString("name", "lefteris");
            object.putString("surname", "chatzilaris");
            object.putString("age", "25");
            object.putString("id", "64971234");
            client2.sendMessage("123", "456", "leadsTeam", object);

            object.putString("name", "vaggelis");
            object.putString("surname", "vazaios");
            object.putString("age", "25");
            object.putString("id", "53241234");
            client3.sendMessage("123", "456", "leadsTeam", object);


            WebServiceClient clientMonitor = new WebServiceClient();


            try {
                clientMonitor.monitorQueryResults(1, "angelos", 2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // wait 2 seconds for messages from websocket
            Thread.sleep(2000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
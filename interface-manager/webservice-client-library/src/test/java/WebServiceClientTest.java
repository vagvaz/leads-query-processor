import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vagvaz on 8/23/14.
 */
public class WebServiceClientTest {
    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException {
        host = "http://localhost";
        port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            WebServiceClient.initialize(host, port);
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

        QueryStatus status = WebServiceClient.submitQuery("vagvaz",
                                                             "SELECT url,domainName from webpages where sentiment=0.8 group by group order by url limit 5");
        QueryStatus currentStatus = WebServiceClient.getQueryStatus(status.getId());
        System.out.println("s: " + status.toString());
        System.out.println("o: " + currentStatus.toString());

    }
}

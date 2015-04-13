import eu.leads.processor.web.WP4Client;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;

/**
 * Created by vagvaz on 4/13/15.
 */
public class WP4ClientTest {
   public static void main(String[] args) throws IOException {
      String inputJSON = "{\n" +
                                 "  \"destination\" :\"0\",\n" +
                                 "  \"stages\" : {\n" +
                                 "    \"myoutput.orderBy\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy\",\n" +
                                 "      \"output\" : \"myoutput\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having\" ],\n" +
                                 "      \"k\" : \"0.01\",\n" +
                                 "      \"q\" : \"0.0001\",\n" +
                                 "      \"stage\" : \"7\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project.groupby\" : {\n" +
                                 "      \n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project.groupby\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having.project\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having.project.groupby.filter-where\" ],\n" +
                                 "      \"k\" : \"0.1\",\n" +
                                 "      \"q\" : \"0.001\",\n" +
                                 "      \"stage\" : \"4\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project.groupby.filter-where.join\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where.join\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readLeft\", \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readRight\" ],\n" +
                                 "      \"k\" : \"0.4\",\n" +
                                 "      \"q\" : \"7\",\n" +
                                 "      \"stage\" : \"2\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readLeft\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readLeft\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where.join\",\n" +
                                 "      \"keyspace\" : [\"webpages\"],\n" +
                                 "      \"k\" : \"1\",\n" +
                                 "      \"q\" : \"1\",\n" +
                                 "      \"stage\" : \"1\"\n" +
                                 "    },\n" +
                                 "    \"myFirstCache\" : {\n" +
                                 "      \"name\" : \"myoutput\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy\" ],\n" +
                                 "      \"output\" : null,\n" +
                                 "      \"k\" : \"1\",\n" +
                                 "      \"q\" : \"1\",\n" +
                                 "      \"stage\" : \"8\"      \n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readRight\" : {\n" +
                                 "\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where.join.readRight\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where.join\",\n" +
                                 "      \"keyspace\" : [\"entiies\" ],\n" +
                                 "      \"k\" : \"1\",\n" +
                                 "      \"q\" : \"1\",\n" +
                                 "      \"stage\" : \"1\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having.project.groupby\" ],\n" +
                                 "      \"k\" : \"0.002\",\n" +
                                 "      \"q\" : \"0.0005\",\n" +
                                 "      \"stage\" : \"5\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having.project.groupby.filter-where\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having.project.groupby.filter-where\",\n" +
                                 "      \"output\" : \"myoutput.orderBy.filter-having.project.groupby\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having.project.groupby.filter-where.join\" ],\n" +
                                 "      \"k\" : \"0.005\",\n" +
                                 "      \"q\" : \"1.7\",\n" +
                                 "      \"stage\" : \"3\"\n" +
                                 "    },\n" +
                                 "    \"myoutput.orderBy.filter-having\" : {\n" +
                                 "      \"name\" : \"myoutput.orderBy.filter-having\",\n" +
                                 "      \"output\" : \"myoutput.orderBy\",\n" +
                                 "      \"keyspace\" : [ \"myoutput.orderBy.filter-having.project\" ],\n" +
                                 "      \"k\" : \"0.0004\",\n" +
                                 "      \"q\" : \"0.0003\",\n" +
                                 "      \"stage\" : \"6\"\n" +
                                 "    }\n" +
                                 "  }\n" +
                                 "  \n" +
                                 "}";

      WP4Client.initialize(" http://127.0.0.1","5001");
      JsonObject reply = WP4Client.evaluatePlan(new JsonObject(inputJSON));
      System.err.println("replied " + reply.encodePrettily());
   }
}
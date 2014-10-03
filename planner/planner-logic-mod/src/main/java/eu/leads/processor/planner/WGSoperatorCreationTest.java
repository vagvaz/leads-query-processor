package eu.leads.processor.planner;

import com.google.common.base.Strings;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.plan.*;
import eu.leads.processor.imanager.IManagerConstants;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.catalog.Schema;
import org.apache.tajo.catalog.TableDesc;
import org.apache.tajo.catalog.TableMeta;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.engine.planner.logical.ScanNode;
import org.apache.tajo.util.KeyValueSet;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.tajo.catalog.proto.CatalogProtos.StoreType;

/**
 * Created by vagvaz on 10/2/14.
 */
public class WGSoperatorCreationTest {

  public static void main(String[] args) {
    Map<String,String> queriesCache = new HashMap<>();
    //InCall
    JsonObject queryIncall = new JsonObject();
    queryIncall.putString("user","test");
    queryIncall.putString("depth","3");
    queryIncall.putString("url", "http://bbc.co.uk");
    //In Webservice
    String query = queryIncall.toString();
    if (Strings.isNullOrEmpty(query) || query.equals("{}")) {

    }
    Action action = new Action();
    action.setId("request-id");
    action.setCategory(StringConstants.ACTION);
    action.setLabel(IManagerConstants.SUBMIT_SPECIAL);
    action.setOwnerId("owner-id");
    action.setComponentType("webservice");
    action.setTriggered("");
    action.setTriggers(new JsonArray());
    JsonObject queryRequest = new JsonObject();
    queryRequest.putObject("query", new JsonObject(query));
    queryRequest.putString("type", "rec_call");
    action.setData(queryRequest);
    action.setDestination(StringConstants.IMANAGERQUEUE);
    action.setStatus(ActionStatus.PENDING.toString());

    //In Imanager
    Action result = action;
    JsonObject actionResult = new JsonObject();
    JsonObject q = action.getData().getObject("query");
    String url = q.getString("url");
    String user = q.getString("user");
    int depth = Integer.parseInt(q.getString("depth"));
    String uniqueId = "my-test-unique-id";
    RecursiveCallQuery queryInMan = new RecursiveCallQuery(user, url, depth);
    queryInMan.setId(uniqueId);
    QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
    queryInMan.setQueryStatus(status);
    QueryContext context = new QueryContext(uniqueId);
    queryInMan.setContext(context);
    JsonObject queryStatus = status.asJsonObject();
    queriesCache.put(uniqueId, queryInMan.asJsonObject().toString());
    actionResult.putObject("query", queryInMan.asJsonObject());
    actionResult.putObject("status",status.asJsonObject());
    result.setResult(actionResult);
    result.setStatus(ActionStatus.COMPLETED.toString());

    //Inp0lanner
    SpecialQuery specialQuery = new SpecialQuery(result.getData().getObject("query"));
    RecursiveCallQuery requery = new RecursiveCallQuery(specialQuery);
    ScanNode node = new ScanNode(0);
    Path testPath = new Path("adfasdfasd");
    TableMeta meta = new TableMeta(StoreType.SEQUENCEFILE,new KeyValueSet());
    TableDesc desc = new TableDesc("default.webpages",getWebpagesSchema(),meta, testPath );
    node.init(desc);
//    node.setInSchema(getWebpagesSchema());
//    node.setOutSchema(node.getInSchema());
    WGSUrlDepthNode rootNode = new WGSUrlDepthNode(1);
    rootNode.setUrl(requery.getUrl());
    rootNode.setDepth(requery.getDepth());
    rootNode.setChild(node);
    SQLPlan plan = new SQLPlan(requery.getId(), rootNode);
    Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
    candidatePlans.add(plan);
    requery.setPlan(plan);
    System.out.println("==== WGS PLAN ====");
    System.out.println(plan.asJsonObject().encodePrettily());
    //InExecutor

  }

  private static Schema getWebpagesSchema() {
    Schema webPagesSchema = new Schema();
    webPagesSchema.addColumn("url", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("domainname", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("headers", TajoDataTypes.Type.BLOB);
    webPagesSchema.addColumn("content", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("responsecode", TajoDataTypes.Type.INT4);
    webPagesSchema.addColumn("language", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("charset", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("responsetime", TajoDataTypes.Type.INT4);
    webPagesSchema.addColumn("links", TajoDataTypes.Type.TEXT_ARRAY);
    webPagesSchema.addColumn("title", TajoDataTypes.Type.TEXT);
    webPagesSchema.addColumn("version", TajoDataTypes.Type.DATE);
    return webPagesSchema;

  }
}

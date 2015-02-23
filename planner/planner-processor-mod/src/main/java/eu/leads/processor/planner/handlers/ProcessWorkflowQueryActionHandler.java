package eu.leads.processor.planner.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import leads.tajo.module.TaJoModule;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.algebra.*;
import org.apache.tajo.catalog.*;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.plan.logical.LogicalRootNode;
import org.apache.tajo.session.Session;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagvaz on 8/19/14.
 */
public class ProcessWorkflowQueryActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private final TaJoModule module;
    private Cache<String,String> queriesCache;
    public ProcessWorkflowQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                             String id, TaJoModule module) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        this.module = module;
       queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
    }

    @Override
    public Action process(Action action) {
        Action result = action;
        JsonObject actionData = action.getData();
        WorkflowQuery workflowQuery = new WorkflowQuery(actionData.getObject("query"));

        //validate and create first plan
        Expr expr = JsonHelper.fromJson(workflowQuery.getWorkflow(), Expr.class);
//        try {
//            expr = module.parseQuery(sqlQuery.getSQL());
//        } catch (Exception e) {
//            failQuery(e, sqlQuery);
//            result.setResult(createFailResult(e, sqlQuery));
//            return result;
//        }

        //Optimize plan
        String planAsString = null;
      boolean hasMapReduce = false;
      ArrayList<JsonObject> mapReduceOpConfigurations = new ArrayList<JsonObject>(0);
        try {
            Session session =
                new Session(workflowQuery.getId(), workflowQuery.getUser(),StringConstants.DEFAULT_DATABASE_NAME);
            while(expr.getType()== OpType.Filter) { //map reduce@
                Expr qual = ((Selection)expr).getQual();
                if(qual.getType() == OpType.ValueList) {
                    hasMapReduce = true;
                    JsonObject MRConfiguration = new JsonObject();
                    ValueListExpr mapReduceData = (ValueListExpr) qual;
                    Expr[] mapReduceConfiguration = mapReduceData.getValues();

                  for(Expr e : mapReduceConfiguration){
                    NamedExpr namedExpr = (NamedExpr)e;
                    if(namedExpr.getAlias().equals("MapperFunction")){
                      MRConfiguration = getMapperConfig(namedExpr,MRConfiguration);
                    }
                    else if (namedExpr.getAlias().equals("ReducerFunction")){
                      MRConfiguration = getReducerConfig(namedExpr,MRConfiguration);
                    }
                    else if (namedExpr.getAlias().equals("JarPathFunction")){
                      MRConfiguration = getJarPathFunction(namedExpr,MRConfiguration);
                    }
                    else if (namedExpr.getAlias().equals("MRConfPathFunction")){
                      MRConfiguration = getMRConfPathFunction(namedExpr,MRConfiguration);
                    }
                      else if(namedExpr.getAlias().contains("AfterTable")){
                        //create new temporatyTable in catalog
                        MRConfiguration = getMROutPut(namedExpr,MRConfiguration);
                    }
                  }
                  mapReduceOpConfigurations.add(MRConfiguration);
                }

                expr=((Selection) expr).getChild();
            }
            planAsString = module.Optimize(session, expr);
        } catch (Exception e) {
            failQuery(e, workflowQuery);
            result.setResult(createFailResult(e, workflowQuery));
            return result;
        }
        LogicalRootNode n = CoreGsonHelper.fromJson(planAsString, LogicalRootNode.class);
        WorkflowPlan plan = new WorkflowPlan(workflowQuery.getId(), n);
        if(hasMapReduce && !mapReduceOpConfigurations.isEmpty()){
          plan.injectMapReduce(mapReduceOpConfigurations.get(0)); //TODO FIX FOR MORE THAN 1 MR
        }
        Set<WorkflowPlan> candidatePlans = new HashSet<WorkflowPlan>();
        candidatePlans.add(plan);
        Set<WorkflowPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
        WorkflowPlan selectedPlan = choosePlan(evaluatedPlans);
        workflowQuery.setPlan(selectedPlan);
        //Inform scheduler for the selected plan.
        JsonObject actionResult = new JsonObject();
        actionResult.putString("status", "ok");
        actionResult.putObject("query", workflowQuery.asJsonObject());
        result.setStatus(ActionStatus.COMPLETED.toString());
        result.setResult(actionResult);
        return result;
    }

    private String CreateTempTable(NamedExpr namedExpr){
        //
        // catalog.existsTable(tableName)
        String ip = "localhost";
        int port =5998;
        CatalogClient catalog;
        try {
            catalog = new CatalogClient(new TajoConf(), ip, port);
            System.out
                    .println("Connection to Catalog Server " + ip + ':' + port + " initialized !");
        } catch (IOException e) {
            catalog = null;
            System.out.println("Unable to connect to the catalog Server" + ip + ':' + port);
            e.printStackTrace();
            return null;
        }
        String[] tableNameArray=namedExpr.getAlias().split(":");
        String originalTableName=tableNameArray[1];
        String tableName = originalTableName;
        ColumnReferenceExpr[] NewColumns = (ColumnReferenceExpr[]) ((ValueListExpr)namedExpr.getExpr()).getValues();

        int i=0;
        if(catalog.existsTable(tableName))
        {
            tableName+= tableNameArray[2];
            System.out.println("CreateNewMRTempArray" + tableName);
            TableDesc tD=catalog.getTableDesc(TajoConstants.DEFAULT_DATABASE_NAME,originalTableName);
            Schema sc = tD.getSchema();
            tD.setName(tableName);

            String colName;
            for(ColumnReferenceExpr col : NewColumns){
                colName = col.getName().split(".")[1];
                System.out.println("Search for column " + colName);
                if(sc.containsByName(colName)){
                    System.out.print(" Column exists ");
                }else{
                    System.out.print(" New Column ");
                    TajoDataTypes.Type columnType = TajoDataTypes.Type.INT4;
                    if(col.getQualifier().equals("Text"))
                        columnType=TajoDataTypes.Type.TEXT;
                    else if (col.getQualifier().equals("Numeric"))
                        columnType=TajoDataTypes.Type.FLOAT8;
                    sc.addColumn(colName,columnType);
                }
            }
            tD.setSchema(sc);
            catalog.createTable(tD);
            return tableName;

        }else{
            return null;
        }

    }
    private JsonObject getMROutPut(NamedExpr namedExpr,
                                             JsonObject mapReduceOpConfiguration) {

        JsonObject result = mapReduceOpConfiguration;
        String newTableName = CreateTempTable(namedExpr);
        if(newTableName!=null) {
            JsonObject tmp = new JsonObject(namedExpr.toJson());
            result.putObject("After", tmp.getObject("Expr"));
            result.putString("OutPutOnTempTable",newTableName);
        }else{
            System.err.println("MR configuration: Unable to find original table name");
        }
        return result;
    }
    private AlterTableDesc createMockAlterTableAddColumn(String Database, String tableName, String columnName, String DataType){
        TajoDataTypes.Type columnType = TajoDataTypes.Type.INT4;
        if(DataType.equals("Text"))
            columnType=TajoDataTypes.Type.TEXT;
        else if (DataType.equals("Numeric"))
            columnType=TajoDataTypes.Type.FLOAT8;
        org.apache.tajo.catalog.AlterTableDesc alterTableDesc = new AlterTableDesc();
        alterTableDesc.setTableName(Database + "." + tableName);
                alterTableDesc.setAddColumn(new Column(columnName,columnType));
        alterTableDesc.setAlterTableType(AlterTableType.ADD_COLUMN);
        return alterTableDesc;
    }

  private JsonObject getMRConfPathFunction(NamedExpr namedExpr,
                                            JsonObject mapReduceOpConfiguration) {
    JsonObject result = mapReduceOpConfiguration;
    JsonObject tmp  =  new JsonObject(namedExpr.toJson());
    result.putObject("config",tmp.getObject("Expr"));
    return result;
  }

  private JsonObject getJarPathFunction(NamedExpr namedExpr, JsonObject mapReduceOpConfiguration) {
    JsonObject result = mapReduceOpConfiguration;
    JsonObject tmp  =  new JsonObject(namedExpr.toJson());
    result.putObject("jarPath",tmp.getObject("Expr"));
    return result;
  }

  private JsonObject getReducerConfig(NamedExpr namedExpr, JsonObject mapReduceOpConfiguration) {
    JsonObject result = mapReduceOpConfiguration;
    JsonObject tmp  =  new JsonObject(namedExpr.toJson());
    result.putObject("reducer",tmp.getObject("Expr"));
    return result;
  }

  private JsonObject getMapperConfig(NamedExpr namedExpr, JsonObject mapReduceOpConfiguration) {
    JsonObject result = mapReduceOpConfiguration;
    JsonObject tmp  =  new JsonObject(namedExpr.toJson());
    result.putObject("mapper",tmp.getObject("Expr"));
    return result;
  }

  private WorkflowPlan choosePlan(Set<WorkflowPlan> evaluatedPlans) {
        //Iterate over the evaluated plans and use a heuristic method to choose a plan.
        WorkflowPlan plan = evaluatedPlans.iterator().next();
        return plan;
    }

    private Set<WorkflowPlan> evaluatePlansFromScheduler(Set<WorkflowPlan> candidatePlans) {
        //Transform each plan to scheduler like format.
        //Annotate each operator with k,q
        //Send Request to Scheduler and receive Evaluations.
        return candidatePlans;
    }

    private JsonObject createFailResult(Exception e, Query query) {
        JsonObject ob = new JsonObject();
        ob.putString("status", "fail");
        ob.putString("message", e.getMessage());
        return ob;

    }

    private void failQuery(Exception e, WorkflowQuery query) {
        QueryStatus status = query.getQueryStatus();
        status.setErrorMessage(e.getMessage());
        status.setStatus(QueryState.FAILED);
        query.setQueryStatus(status);

        queriesCache.put(query.getId(), query.asJsonObject().toString());
    }
}

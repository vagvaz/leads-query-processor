package eu.leads.processor.planner.handlers;

import com.google.gson.Gson;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import eu.leads.processor.planner.PlanUtils;
import eu.leads.processor.web.WP4Client;
import leads.tajo.module.TaJoModule;
import org.apache.tajo.algebra.*;
import org.apache.tajo.catalog.Column;
import org.apache.tajo.catalog.Schema;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.plan.PlanningException;
import org.apache.tajo.plan.logical.LogicalRootNode;
import org.apache.tajo.session.Session;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vagvaz on 8/19/14.
 */
public class ProcessSQLQueryActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private String currentCluster;
    private final TaJoModule module;
    private Cache<String,String> queriesCache;
    private Cache statisticsCache;
    private  Session session;
    private WP4Client wp4Client;
    private JsonObject globalInformation;
    private String schedHost;
    private String schedPort;

    public ProcessSQLQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                        String id, TaJoModule module,
                                        String schedHost,String schedPort,JsonObject globalInformation) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        this.module = module;
        queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
        statisticsCache = (Cache) persistence.getPersisentCache(StringConstants.STATISTICS_CACHE);
        session = new Session("defaultQueryId", "defaultUser",StringConstants.DEFAULT_DATABASE_NAME);
//        WP4Client.initialize(schedHost,schedPort);
        this.schedHost  = schedHost;
        this.schedPort = schedPort;
        currentCluster = LQPConfiguration.getInstance().getMicroClusterName();
        this.globalInformation = globalInformation;
    }

    @Override
    public Action process(Action action) {
        Action result = action;
        JsonObject actionData = action.getData();
        SQLQuery sqlQuery = new SQLQuery(actionData.getObject("query"));

        //validate and create first plan
        Expr expr = null;
        try {
            expr = module.parseQuery(sqlQuery.getSQL());


            if(expr.getType().equals(OpType.CreateTable)){
                module.createTable((CreateTable)expr);
                result.setResult(ignoreResult(sqlQuery));
                completeQuery(sqlQuery);
                return result;
            }

            else if(expr.getType().equals(OpType.DropTable)){
                module.dropTable((DropTable)expr);
                result.setResult(ignoreResult(sqlQuery));
                completeQuery(sqlQuery);
                return result;
            }
        } catch (Exception e) {
            failQuery(e, sqlQuery);
            result.setResult(createFailResult(e, sqlQuery));
            return result;
        }
        //Optimize plan
//        String planAsString = null;
//        try {
//            planAsString = module.Optimize(session, expr);
//        } catch (Exception e) {
//            failQuery(e, sqlQuery);
//            result.setResult(createFailResult(e, sqlQuery));
//            return result;
//        }
//        LogicalRootNode n = CoreGsonHelper.fromJson(planAsString, LogicalRootNode.class);
//        SQLPlan plan = new SQLPlan(sqlQuery.getId(), n);
        SQLPlan plan = null;
        try {
            plan = getLogicaSQLPlan(expr,sqlQuery);
            System.out.println("\nPlan:" + plan.toString());
        } catch (Exception e) {
            failQuery(e, sqlQuery);
            result.setResult(createFailResult(e, sqlQuery));
            return result;
        }
        if(plan == null){
            failQuery(new Exception("Could not Create Plan"),sqlQuery);
            result.setResult(createFailResult(new Exception("Unable to create plan due to internal error"),sqlQuery));
            return result;
        }
        if(expr.getType().equals(OpType.CreateIndex))
        {
            sqlQuery.setPlan(plan);
        }else {
            Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
            candidatePlans.add(plan);
            Set<SQLPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
            SQLPlan selectedPlan = choosePlan(evaluatedPlans);
            sqlQuery.setPlan(selectedPlan);
        }
        //Inform scheduler for the selected plan.
        JsonObject actionResult = new JsonObject();
        actionResult.putString("status", "ok");
        actionResult.putObject("query", sqlQuery.asJsonObject());
        result.setStatus(ActionStatus.COMPLETED.toString());
        result.setResult(actionResult);
        return result;
    }



    private JsonObject ignoreResult(SQLQuery sqlQuery) {
        JsonObject ob = new JsonObject();
        ob.putString("status", "ignore");
        ob.putString("message", sqlQuery.getSQL());
        return ob;
    }

    private SQLPlan getLogicaSQLPlan(Expr expr, SQLQuery sqlQuery) throws PlanningException {
        SQLPlan result = null;

        try {
            if (expr.getType().equals(OpType.Insert)) {
                result = createInsertSQLPlan(session, expr, sqlQuery);
                return result;
            }else if (expr.getType().equals(OpType.CreateIndex)) {
                result = createIndexSQLPlan2(session, expr, sqlQuery);
                return result;
            }
        }catch (Exception e){
            throw e;
        }
        //Optimize plan
        String planAsString = null;

        try {
            planAsString = module.Optimize(session, expr);
        } catch (Exception e) {
            throw e;
        }
        LogicalRootNode n = CoreGsonHelper.fromJson(planAsString, LogicalRootNode.class);
        result = new SQLPlan(sqlQuery.getId(), n);
        return result;
    }
    private SQLPlan createInsertSQLPlan(Session session, Expr expr, SQLQuery sqlQuery) throws PlanningException {
        SQLPlan result = new SQLPlan();
        LogicalRootNode rootNode = new LogicalRootNode(1);
        Insert opInsert = (Insert)expr;
        Expr subexpr = opInsert.getSubQuery();

//        insertNode.setInSchema(opInsert);
        try {
            String prelimPlan = TaJoModule.Optimize( session,subexpr);
            Gson gson = new Gson();
            LogicalRootNode n = CoreGsonHelper.fromJson(prelimPlan, LogicalRootNode.class);
            result = new SQLPlan(sqlQuery.getId(),n);
            PlanNode node = result.getNode(result.getQueryId()+".0");
            String snode = node.toString();
            PlanNode node2 = new PlanNode(new JsonObject(snode));
            if(node.equals(node2))
                System.out.print("EQQULALL");
            else
                System.err.print("notEQQULALL");
            node.getConfiguration().getObject("body").putString("operationType", OpType.Insert.toString());
            if(opInsert.getTableName().startsWith(StringConstants.DEFAULT_DATABASE_NAME))
                node.getConfiguration().getObject("body").putString("tableName",opInsert.getTableName());
            else
            if(opInsert.getTableName().contains("."))
                node.getConfiguration().getObject("body").putString("tableName",opInsert.getTableName());
            else
                node.getConfiguration().getObject("body").putString("tableName",StringConstants.DEFAULT_DATABASE_NAME+"."+opInsert.getTableName());

            node.getConfiguration().getObject("body").putArray("primaryColumns",resolvePrimaryColumns(opInsert.getTableName()));
            if(opInsert.hasTargetColumns()) {
                JsonArray array = new JsonArray(opInsert.getTargetColumns());
                node.getConfiguration().getObject("body").putArray("columnNames", array);
            }
            else{
                JsonArray array = new JsonArray();
                Schema tableSchema = TaJoModule.getTableSchema(opInsert.getTableName());
                for(Column c : tableSchema.getColumns()){
                    array.add(c.getSimpleName());
                }
                node.getConfiguration().getObject("body").putArray("columnNames",array);
            }
            result.updateNode(node);
        } catch (PlanningException e) {
            throw e;
        }
        return result;
    }

    private SQLPlan createIndexSQLPlan2(Session session, Expr expr, SQLQuery sqlQuery) throws PlanningException {
        SQLPlan result = new SQLPlan();
        LogicalRootNode rootNode = new LogicalRootNode(1);

        String insertExpr = "{\n" +
                "  \"IsOverwrite\": false,\n" +
                "  \"TableName\": \"adidas_keywords\",\n" +
                "  \"TargetColumns\": [\n" +
                "    \"keywords\"\n" +
                "  ],\n" +
                "  \"SubPlan\": {\n" +
                "    \"IsDistinct\": false,\n" +
                "    \"Projections\": [\n" +
                "      {\n" +
                "        \"Expr\": {\n" +
                "          \"Value\": \"tetst\",\n" +
                "          \"ValueType\": \"String\",\n" +
                "          \"OpType\": \"Literal\"\n" +
                "        },\n" +
                "        \"OpType\": \"Target\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"OpType\": \"Projection\"\n" +
                "  },\n" +
                "  \"OpType\": \"Insert\"\n" +
                "}";

        CreateIndex opIndex = (CreateIndex)expr;


        Insert opInsert = (Insert)JsonHelper.fromJson(insertExpr, Expr.class);//
        Expr subexpr = opInsert.getSubQuery();
        try {
            String prelimPlan = TaJoModule.Optimize( session,subexpr);
            LogicalRootNode n = CoreGsonHelper.fromJson(prelimPlan, LogicalRootNode.class);
            result = new SQLPlan(sqlQuery.getId(),n);
            PlanNode node = result.getNode(result.getQueryId() + ".0");

            node.getConfiguration().getObject("body").putString("operationType",OpType.CreateIndex.toString());
            String tableName = (((Relation) ((Projection) opIndex.getChild()).getChild())).getName();

            if(tableName.startsWith(StringConstants.DEFAULT_DATABASE_NAME))
            node.getConfiguration().getObject("body").putString("tableName",tableName);
            else
                if(tableName.contains("."))
                    node.getConfiguration().getObject("body").putString("tableName",tableName);
                else
                    node.getConfiguration().getObject("body").putString("tableName",StringConstants.DEFAULT_DATABASE_NAME+"."+tableName);

            node.getConfiguration().getObject("body").putArray("primaryColumns",resolvePrimaryColumns(tableName));

            String indexName = opIndex.getIndexName();
            if (indexName.isEmpty())
                indexName = "noname"+ UUID.randomUUID();

            node.getConfiguration().getObject("body").putString("indexName", indexName);

            Sort.SortSpec[] collumns = opIndex.getSortSpecs();
            if(collumns!=null) {
                JsonArray array = new JsonArray();
                for (Sort.SortSpec sc : collumns)
                    array.add(((ColumnReferenceExpr) sc.getKey()).getName());
                node.getConfiguration().getObject("body").putArray("columnNames", array);
            }
            else{
                JsonArray array = new JsonArray();
                Schema tableSchema = TaJoModule.getTableSchema(tableName);
                for(Column c : tableSchema.getColumns()){
                    array.add(c.getSimpleName());
                }
                node.getConfiguration().getObject("body").putArray("columnNames",array);
            }
            node.getConfiguration().putString("rawquery", opIndex.toJson());
            result.updateNode(node);
        } catch (PlanningException e) {
            throw e;
        }
        return result;
    }

    private SQLPlan createIndexSQLPlan(Session session, Expr expr, SQLQuery sqlQuery)  {
        SQLPlan result = null;
        CreateIndex opIndex = (CreateIndex)expr;

       // LogicalRootNode n = new LogicalRootNode(1);



        String testNode = "{\n" +
                "  \"child\": {\n" +
                "    \"type\": \"EXPRS\",\n" +
                "    \"body\": {\n" +
                "      \"exprs\": [\n" +
                "      ],\n" +
                "      \"nodeId\": 0,\n" +
                "      \"type\": \"EXPRS\",\n" +
                "      \"outputSchema\": {\n" +
                "      },\n" +
                "      \"cost\": 0.0\n" +
                "    }\n" +
                "  },\n" +
                "  \"nodeId\": 1,\n" +
                "  \"type\": \"ROOT\",\n" +
                "  \"inputSchema\": {\n" +
                "  },\n" +
                "  \"outputSchema\": {\n" +
                "  },\n" +
                "  \"cost\": 0.0\n" +
                "}";

        LogicalRootNode n = CoreGsonHelper.fromJson(testNode, LogicalRootNode.class);

        result = new SQLPlan(sqlQuery.getId(),n);
//        JsonObject jnode2 = new JsonObject();
//        jnode2.putArray("nodes",new JsonArray());
//        result.copy(jnode2);
        JsonObject jnode = new JsonObject();
        jnode.putObject(result.getQueryId()+".0",  new JsonObject());
        result.setPlanGraph(jnode);
        PlanNode node = result.getNode(result.getQueryId()+".0");
        node.setNodeType(LeadsNodeType.EXPRS);
        JsonObject te=new JsonObject();
        te.putObject("body",new JsonObject());
        node.setConfiguration(te);

        node.getConfiguration().getObject("body").putString("operationType", OpType.CreateIndex.toString());
        String tableName = (((Relation) ((Projection) opIndex.getChild()).getChild())).getName();
        if(tableName.startsWith(StringConstants.DEFAULT_DATABASE_NAME))
            node.getConfiguration().getObject("body").putString("tableName",tableName);
        else
        if(tableName.contains("."))
            node.getConfiguration().getObject("body").putString("tableName",tableName);
        else
            node.getConfiguration().getObject("body").putString("tableName",StringConstants.DEFAULT_DATABASE_NAME+"."+tableName);

        node.getConfiguration().getObject("body").putArray("primaryColumns",resolvePrimaryColumns(tableName));

        String indexName = opIndex.getIndexName();
        if (indexName.isEmpty())
            indexName = "noname"+ UUID.randomUUID();

        node.getConfiguration().getObject("body").putString("indexName", indexName);

        Sort.SortSpec[] collumns = opIndex.getSortSpecs();
        if(collumns!=null) {
            JsonArray array = new JsonArray();
            for (Sort.SortSpec sc : collumns)
                array.add(((ColumnReferenceExpr) sc.getKey()).getName());
            node.getConfiguration().getObject("body").putArray("columnNames", array);
        }
        else{
            JsonArray array = new JsonArray();
            Schema tableSchema = TaJoModule.getTableSchema(tableName);
            for(Column c : tableSchema.getColumns()){
                array.add(c.getSimpleName());
            }
            node.getConfiguration().getObject("body").putArray("columnNames",array);
        }
        node.getConfiguration().putString("rawquery", opIndex.toJson());
        //JsonArray newNodesArray = new JsonArray();

        //result.updateNode(node);
        return result;
    }

    //do it with catalog
    private JsonArray resolvePrimaryColumns(String tableName) {
        String table = tableName;
        Set<String> primaryColumns = null;
        if(table.startsWith(StringConstants.DEFAULT_DATABASE_NAME))
            primaryColumns = TaJoModule.getPrimaryColumn(tableName);
        else
            primaryColumns = TaJoModule.getPrimaryColumn(StringConstants.DEFAULT_DATABASE_NAME+"." + table);
        if(tableName.equals(StringConstants.DEFAULT_DATABASE_NAME+".webpages") || tableName.equals("webpages"))
        {
            primaryColumns = new HashSet<>();
            primaryColumns.add("url");
        }
        else if(tableName.equals(StringConstants.DEFAULT_DATABASE_NAME+".entities") || tableName.equals("entities")){
            primaryColumns = new HashSet<>();
            primaryColumns.add("webpageurl");
            primaryColumns.add("name");
        }
        else{

        }
        JsonArray result = new JsonArray(primaryColumns.toArray());
        return result;
    }


    private SQLPlan choosePlan(Set<SQLPlan> evaluatedPlans) {
        //Iterate over the evaluated plans and use a heuristic method to choose a plan.

        SQLPlan plan = evaluatedPlans.iterator().next();
        return plan;
    }

    private Set<SQLPlan> evaluatePlansFromScheduler(Set<SQLPlan> candidatePlans) {
        //Transform each plan to scheduler like format.
        //Annotate each operator with k,q
        //Send Request to Scheduler and receive Evaluations.
        Set<SQLPlan> result = new HashSet<>();
        for(SQLPlan plan : candidatePlans){
            JsonObject p = plan.getPlanGraph().copy();
            p = PlanUtils.handleRootOutputNodes(p);
            p = PlanUtils.updateKeyspaceParameter(p);
            p = PlanUtils.numberStages(p);
            p = PlanUtils.annotatePlan(statisticsCache, p);
            JsonObject annotatedPlan = null;
//            try {
                JsonObject schedulerRep = PlanUtils.getSchedulerRep(p,currentCluster);
                System.err.println("$$$$$$$$$$$$$$$$$$$$\n"+schedulerRep.encodePrettily());
                annotatedPlan = PlanUtils.emulateScheduler(schedulerRep,globalInformation);
//                annotatedPlan = WP4Client.evaluatePlan(schedulerRep,schedHost,schedPort);
//            } catch (IOException e) {
//                log.error("Exception  e " + e.getMessage());
//              SQLQuery query = new SQLQuery( new JsonObject(queriesCache.get(plan.getQueryId())));
//              failQuery(new Exception("Could not access the scheduler"),query);            }
//            if(annotatedPlan == null){
//                SQLQuery query = new SQLQuery( new JsonObject(queriesCache.get(plan.getQueryId())));
//                failQuery(new Exception("Could not access the scheduler"),query);
//                return result;
//            }
            JsonObject updatedPlan = PlanUtils.updateInformation(plan.getPlanGraph(),annotatedPlan.getObject("stages"),globalInformation);
            updatedPlan = PlanUtils.updateTargetEndpoints(updatedPlan);
            System.err.println(updatedPlan.encodePrettily());
            plan.setPlanGraph(updatedPlan);
            result.add(plan);
        }
        return result;
    }

    private JsonObject createFailResult(Exception e, SQLQuery sqlQuery) {
        JsonObject ob = new JsonObject();
        ob.putString("status", "fail");
        ob.putString("message", e.getMessage());
        return ob;

    }

    private void failQuery(Exception e, SQLQuery sqlQuery) {
        QueryStatus status = sqlQuery.getQueryStatus();
        status.setErrorMessage(e.getMessage());
        status.setStatus(QueryState.FAILED);
        sqlQuery.setQueryStatus(status);
        queriesCache.put(sqlQuery.getId(), sqlQuery.asJsonObject().toString());
    }

    private void completeQuery(SQLQuery sqlQuery) {
        QueryStatus status = sqlQuery.getQueryStatus();
        status.setErrorMessage("");
        status.setStatus(QueryState.COMPLETED);
        sqlQuery.setQueryStatus(status);
        queriesCache.put(sqlQuery.getId(), sqlQuery.asJsonObject().toString());
    }
}

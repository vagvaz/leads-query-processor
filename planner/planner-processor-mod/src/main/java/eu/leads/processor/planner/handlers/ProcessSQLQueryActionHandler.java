package eu.leads.processor.planner.handlers;

import com.google.gson.Gson;
        import eu.leads.processor.common.StringConstants;
        import eu.leads.processor.common.infinispan.InfinispanManager;
        import eu.leads.processor.core.Action;
        import eu.leads.processor.core.ActionHandler;
        import eu.leads.processor.core.ActionStatus;
        import eu.leads.processor.core.comp.LogProxy;
        import eu.leads.processor.core.net.Node;
        import eu.leads.processor.core.plan.*;
        import leads.tajo.module.TaJoModule;
import org.apache.tajo.algebra.*;
import org.apache.tajo.catalog.Column;
        import org.apache.tajo.catalog.Schema;
        import org.apache.tajo.engine.json.CoreGsonHelper;
        import org.apache.tajo.engine.planner.PlanningException;
        import org.apache.tajo.engine.planner.logical.LogicalRootNode;
        import org.apache.tajo.master.session.Session;
        import org.infinispan.Cache;
        import org.vertx.java.core.json.JsonArray;
        import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagvaz on 8/19/14.
 */
public class ProcessSQLQueryActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private final TaJoModule module;
    private Cache<String,String> queriesCache;
    private static Session session;
    public ProcessSQLQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                           String id, TaJoModule module) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        this.module = module;
        queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
        session = new Session("defaultQueryId", "defaultUser",StringConstants.DEFAULT_DATABASE_NAME);
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
        Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
        candidatePlans.add(plan);
        Set<SQLPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
        SQLPlan selectedPlan = choosePlan(evaluatedPlans);
        sqlQuery.setPlan(selectedPlan);
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
            node.getConfiguration().getObject("body").putString("operationType", OpType.Insert.toString());
            if(opInsert.getTableName().startsWith(StringConstants.DEFAULT_DATABASE_NAME))
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
        return candidatePlans;
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

package eu.leads.processor.planner.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.core.plan.SQLPlan;
import eu.leads.processor.core.plan.SQLQuery;
import leads.tajo.module.TaJoModule;
import org.apache.tajo.algebra.Expr;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.engine.planner.logical.LogicalRootNode;
import org.apache.tajo.master.session.Session;
import org.infinispan.Cache;
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
    public ProcessSQLQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
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
        SQLQuery sqlQuery = new SQLQuery(actionData.getObject("query"));

        //validate and create first plan
        Expr expr = null;
        try {
            expr = module.parseQuery(sqlQuery.getSQL());
        } catch (Exception e) {
            failQuery(e, sqlQuery);
            result.setResult(createFailResult(e, sqlQuery));
            return result;
        }

        //Optimize plan
        String planAsString = null;
        try {
            Session session =
                new Session(sqlQuery.getId(), sqlQuery.getUser(), StringConstants.DEFAULT_DB_NAME);
            planAsString = module.Optimize(session, expr);
        } catch (Exception e) {
            failQuery(e, sqlQuery);
            result.setResult(createFailResult(e, sqlQuery));
            return result;
        }
        LogicalRootNode n = CoreGsonHelper.fromJson(planAsString, LogicalRootNode.class);
        SQLPlan plan = new SQLPlan(sqlQuery.getId(), n);
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
}

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
import org.apache.tajo.algebra.*;
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
        try {
            Session session =
                new Session(workflowQuery.getId(), workflowQuery.getUser(),StringConstants.DEFAULT_DATABASE_NAME);
            if(expr.getType()== OpType.Filter) {
                Expr qual = ((Selection)expr).getQual();
                if(qual.getType() == OpType.ValueList) {
                    ValueListExpr mapReduceData = (ValueListExpr) qual;
                    //VAG OLO DIKO SOU !
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

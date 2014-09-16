package eu.leads.processor.planner.handlers;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import leads.tajo.module.TaJoModule;
import org.apache.tajo.engine.planner.logical.LogicalRootNode;
import org.apache.tajo.engine.planner.logical.ScanNode;
import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagvaz on 8/19/14.
 */
public class ProcessSpecialQueryActionHandler implements ActionHandler {
   private final Node com;
   private final LogProxy log;
   private final PersistenceProxy persistence;
   private final String id;
   private final TaJoModule module;

   public ProcessSpecialQueryActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id, TaJoModule module) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
      this.module = module;
   }

   @Override
   public Action process(Action action) {
      Action result = action;
      SpecialQuery specialQuery = new SpecialQuery(action.getData().getObject("query"));
      if(specialQuery.getSpecialQueryType().equals("rec_call")){
         RecursiveCallQuery query = new RecursiveCallQuery(specialQuery);
         ScanNode node = new ScanNode(0);
         node.setInSchema(module.getTableSchema("webpages"));
         node.setOutSchema(node.getInSchema());
         WGSUrlDepthNode rootNode = new WGSUrlDepthNode(1);
         rootNode.setUrl(query.getUrl());
         rootNode.setDepth(query.getDepth());
         rootNode.setChild(node);
         SQLPlan plan = new SQLPlan(query.getId(),rootNode);
         Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
         candidatePlans.add(plan);
         Set<SQLPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
         SQLPlan selectedPlan = choosePlan(evaluatedPlans);
         query.setPlan(selectedPlan);
         JsonObject actionResult = new JsonObject();
         actionResult.putString("status","ok");
         actionResult.putObject("query", query.asJsonObject());
         result.setStatus(ActionStatus.COMPLETED.toString());
         result.setResult(actionResult);
      }
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
}

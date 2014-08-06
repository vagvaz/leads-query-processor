package eu.leads.processor.core.plan;

import eu.leads.processor.core.DataType;
import org.vertx.java.core.json.JsonObject;

import java.util.Collection;

/**
 * Created by vagvaz on 8/4/14.
 */
public class SQLPlan extends DataType implements Plan {
   public SQLPlan(JsonObject plan) {
   }

   public SQLPlan() {
      super();
   }

   @Override
   public void setOutput(PlanNode node) {

   }

   @Override
   public PlanNode getOutput() {
      return null;
   }

   @Override
   public Collection<PlanNode> getNodes() {
      return null;
   }

   @Override
   public PlanNode getNode(String nodeId) {
      return null;
   }

   @Override
   public Collection<String> getSources() {
      return null;
   }
}

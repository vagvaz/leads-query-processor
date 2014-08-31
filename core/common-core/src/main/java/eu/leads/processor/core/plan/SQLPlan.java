package eu.leads.processor.core.plan;

import eu.leads.processor.core.DataType;
import org.apache.tajo.engine.planner.PlannerUtil;
import org.apache.tajo.engine.planner.logical.BinaryNode;
import org.apache.tajo.engine.planner.logical.LogicalNode;
import org.apache.tajo.engine.planner.logical.LogicalRootNode;
import org.apache.tajo.engine.planner.logical.UnaryNode;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by vagvaz on 8/4/14.
 */
public class SQLPlan extends DataType implements Plan {
   public SQLPlan(JsonObject plan) {
      super(plan);
   }

   public SQLPlan() {
      super();
   }
   public SQLPlan(String queryId){
      super();
      setQueryId(queryId);
   }

   public SQLPlan(LogicalRootNode rootNode) {
      super();
      setQueryId("");
      JsonObject planGraph = generatePlan(rootNode);
      JsonObject root = new JsonObject(rootNode.toString());
      setRootNode(root);
      JsonObject nodes = getNodesAllNodesByPid(rootNode);
      data.putObject("nodesByPID",nodes);
      setPlanGraph(planGraph);
   }

   public SQLPlan(String queryId,LogicalRootNode rootNode) {
      super();
      setQueryId(queryId);
      JsonObject planGraph = generatePlan(rootNode);
      JsonObject root = new JsonObject(rootNode.toString());
      setRootNode(root);
      JsonObject nodes = getNodesAllNodesByPid(rootNode);
      data.putObject("nodesByPID",nodes);
      setPlanGraph(planGraph);
   }

   private JsonObject getNodesAllNodesByPid(LogicalRootNode rootNode) {
      JsonObject result = new JsonObject();
      LogicalNode current = rootNode;
      List<LogicalNode> toProcess = new ArrayList<>();
      toProcess.add(current);
      while(toProcess.size() > 0 ){
         current = toProcess.remove(0);
         if( current instanceof UnaryNode){
            UnaryNode currentTmp = (UnaryNode)current;
            LogicalNode n = currentTmp.getChild();
            result.putObject(String.valueOf(n.getPID()), new JsonObject(n.toJson()));
            toProcess.add(n);
         }
         else if(current instanceof BinaryNode){
            BinaryNode currentTmp = (BinaryNode)current;
            LogicalNode l = currentTmp.getLeftChild();
            LogicalNode r = currentTmp.getRightChild();
            result.putObject(String.valueOf(l.getPID()), new JsonObject(l.toJson()));
            toProcess.add(l);
            result.putObject(String.valueOf(r.getPID()), new JsonObject(r.toJson()));
            toProcess.add(r);
         }
      }
      return result;
   }

   private JsonObject generatePlan(LogicalRootNode rootNode) {
      JsonObject result = new JsonObject();
      PlanNode planNode = new PlanNode(rootNode);
      PlanNode outputNode = new PlanNode();
      outputNode.setOutput("");
      outputNode.setNodeType(LeadsNodeType.OUTPUT_NODE);

      LogicalNode current = rootNode;
      List<LogicalNode> toProcess = new ArrayList<>();
      toProcess.add(current);
      while(toProcess.size() > 0 ){
         current = toProcess.remove(0);
         if( current instanceof UnaryNode){
            UnaryNode currentTmp = (UnaryNode)current;
            LogicalNode n = currentTmp.getChild();
            result.putObject(String.valueOf(n.getPID()), new JsonObject(n.toJson()));
            toProcess.add(n);
         }
         else if(current instanceof BinaryNode){
            BinaryNode currentTmp = (BinaryNode)current;
            LogicalNode l = currentTmp.getLeftChild();
            LogicalNode r = currentTmp.getRightChild();
            result.putObject(String.valueOf(l.getPID()), new JsonObject(l.toJson()));
            toProcess.add(l);
            result.putObject(String.valueOf(r.getPID()), new JsonObject(r.toJson()));
            toProcess.add(r);
         }
      }
      return result;
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

   @Override
   public JsonObject getRootNode() {
      return null;
   }

   @Override
   public void setRootNode(LogicalRootNode rootNode) {

   }

   @Override
   public void setRootNode(JsonObject rootNode) {

   }

   @Override
   public JsonObject getPlanGraph() {
      return null;
   }

   @Override
   public void setPlanGraph(JsonObject planGraph) {

   }

   @Override
   public String getQueryId() {
      return null;
   }

   @Override
   public void setQueryId(String queryId) {

   }

   @Override
   public void addParentTo(String nodeId, PlanNode newNode) {

   }

   @Override
   public void addChildTo(String nodeId, PlanNode newNode) {

   }

   @Override
   public JsonObject getNodeById(String id) {
      return null;
   }

   @Override
   public JsonObject getNodeByPid(int pid) {
      return null;
   }
}

package eu.leads.processor.deployer;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.DataType;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.core.plan.NodeStatus;
import eu.leads.processor.core.plan.PlanNode;
import eu.leads.processor.core.plan.SQLPlan;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vagvaz on 9/17/14.
 */
public class ExecutionPlanMonitor {
    private String queryId;
    private SQLPlan plan;
    private ExecutionPlan executionPlan;
    private Set<LeadsNodeType> groupingCandidate;
    private Action action;
    String outputCacheName;
    private boolean isSpecial;


  public SQLPlan getPlan() {
    return plan;
  }

  public void setPlan(SQLPlan plan) {
    this.plan = plan;
  }

  public ExecutionPlan getExecutionPlan() {
    return executionPlan;
  }

  public void setExecutionPlan(ExecutionPlan executionPlan) {
    this.executionPlan = executionPlan;
  }

  public Set<LeadsNodeType> getGroupingCandidate() {
    return groupingCandidate;
  }

  public void setGroupingCandidate(Set<LeadsNodeType> groupingCandidate) {
    this.groupingCandidate = groupingCandidate;
  }

  public String getOutputCacheName() {
    return outputCacheName;
  }

  public void setOutputCacheName(String outputCacheName) {
    this.outputCacheName = outputCacheName;
  }

  public boolean isSpecial() {
    return isSpecial;
  }

  public void setSpecial(boolean isSpecial) {
    this.isSpecial = isSpecial;
  }

  public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean isSorted) {
        this.isSorted = isSorted;
    }

    boolean isSorted = false;
    public ExecutionPlanMonitor(SQLPlan plan) {
        this.queryId = plan.getQueryId();
        this.plan = plan;
       groupingCandidate  = new HashSet<>();
       groupingCandidate.add(LeadsNodeType.GROUP_BY);
       groupingCandidate.add(LeadsNodeType.HAVING);
       groupingCandidate.add(LeadsNodeType.PROJECTION);
       groupingCandidate.add(LeadsNodeType.SCAN);
       groupingCandidate.add(LeadsNodeType.TABLE_SUBQUERY);
       groupingCandidate.add(LeadsNodeType.SELECTION);

    }

    public void complete(PlanNode nodeId) {
       PlanNode node = plan.getNode(nodeId.getNodeId());
       node.setStatus(NodeStatus.COMPLETED);
       LeadsNodeType currentType = node.getNodeType();
        if(currentType.equals(LeadsNodeType.SORT))
            isSorted = true;
       if(currentType != LeadsNodeType.OUTPUT_NODE && currentType != LeadsNodeType.ROOT){
          outputCacheName = node.getNodeId();
       }
    }

    public PlanNode getNextOperator(PlanNode node){
      PlanNode result = null;
      if(node.getNodeType().equals(LeadsNodeType.OUTPUT_NODE))
        return result;
      result = plan.getNode(node.getOutput());
      return result;
    }
    public PlanNode getNextExecutableOperator(PlanNode node) {
       PlanNode result = null;
       if(node.getNodeType().equals(LeadsNodeType.OUTPUT_NODE))
          return result;
       PlanNode next = plan.getNode(node.getOutput());
       if(canBeExecuted(next)){
         if(groupingCandidate.contains(next.getNodeType())){
            result = resolveParameters(next);
         }
         else{
            result = resolveParameters(next);
         }
       }
       else{

       }
       return result;
    }

   private PlanNode resolveParameters(PlanNode node) {
      PlanNode result = new PlanNode(node.asJsonObject());
      List<String> inputs = result.getInputs();
      List<String> arrayList = new ArrayList<String>(inputs.size());
      for(String input : inputs)
      {
         PlanNode tmpNode = plan.getNode(input);
         if(tmpNode.getNodeType().equals(LeadsNodeType.SCAN)){
            String tableName = tmpNode.getConfiguration().getObject("body").getObject("tableDesc").getString("tableName");
            arrayList.add(tableName);
         }
         else if(tmpNode.getNodeType().equals(LeadsNodeType.TABLE_SUBQUERY)){
            String inputName = tmpNode.getInputs().get(0);
            arrayList.add(inputName);
         }
      }
     if(result != null && !result.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)
                       && !result.getNodeType().equals(LeadsNodeType.ROOT)
                       && !result.getNodeType().equals(LeadsNodeType.TABLE_SUBQUERY))
     {
       result.getConfiguration().putBoolean("isSorted",isSorted());
     }
      return result;
   }

   private boolean canBeExecuted(PlanNode node) {

      List<String> inputs = node.getInputs();
      boolean result = true;
      for(String input : inputs){
         PlanNode inputNode = plan.getNode(input);
         if(!(inputNode.getStatus().equals(NodeStatus.COMPLETED)))
         {
            result = false;
         }
      }
      return result;
   }

   public boolean isFullyExecuted() {
      boolean result = true;
      JsonObject planNodes = plan.getPlanGraph();
      for(String nodeId : planNodes.getFieldNames()){
        PlanNode node = plan.getNode(nodeId);
        if(!node.getStatus().equals(NodeStatus.COMPLETED))
        {
           result = false;
           break;
        }
      }
      return result;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public void fail(PlanNode node) {

    }
    public List<PlanNode> getSources(){
       List<PlanNode> result = new ArrayList<PlanNode>();
       JsonObject planNodes = plan.getPlanGraph();
       for(String nodeId : planNodes.getFieldNames()){
          PlanNode node = plan.getNode(nodeId);
          if(node.getNodeType().equals(LeadsNodeType.SCAN)){
             result.add(node);
          }
       }
       return result;
    }
    public DataType getLogicalPlan() {
        return this.plan;
    }
    public DataType getPhysicalPlan() {return this.executionPlan;}

   public void setAction(Action action) {
      this.action = action;
   }
   public Action getAction(){
      return action;
   }

   public String getCacheName() {
      return outputCacheName;
   }
}

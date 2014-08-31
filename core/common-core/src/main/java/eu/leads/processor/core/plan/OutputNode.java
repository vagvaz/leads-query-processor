package eu.leads.processor.core.plan;

import org.apache.tajo.engine.planner.PlanString;
import org.apache.tajo.engine.planner.logical.LogicalNode;
import org.apache.tajo.engine.planner.logical.LogicalNodeVisitor;
import org.apache.tajo.engine.planner.logical.LogicalRootNode;

/**
 * Created by vagvaz on 8/29/14.
 */
public class OutputNode extends LogicalRootNode {

   public OutputNode(int pid) {
      super(pid);
   }

   LeadsNodeType getNodeType(){
      return LeadsNodeType.OUTPUT_NODE;
   }
}

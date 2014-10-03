package eu.leads.processor.core.plan;

import org.apache.tajo.engine.planner.logical.LogicalRootNode;

/**
 * Created by vagvaz on 10/2/14.
 */
public class EncryptedPointQueryNode extends LogicalRootNode {
  public EncryptedPointQueryNode(int pid) {
    super(pid);
  }
}

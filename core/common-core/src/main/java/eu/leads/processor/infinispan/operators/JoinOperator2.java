package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;

/**
 * Created by vagvaz on 11/21/14.
 */
public class JoinOperator2 extends MapReduceOperator {
   public JoinOperator2(Node com, InfinispanManager persistence, LogProxy log, Action action) {
      super(com, persistence, log, action);
   }
}

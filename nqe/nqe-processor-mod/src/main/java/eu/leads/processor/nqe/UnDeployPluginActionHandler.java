package eu.leads.processor.nqe;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;

/**
 * Created by vagvaz on 9/23/14.
 */
public class UnDeployPluginActionHandler implements ActionHandler {
   public UnDeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {

   }

   @Override
   public Action process(Action action) {
      return null;
   }
}

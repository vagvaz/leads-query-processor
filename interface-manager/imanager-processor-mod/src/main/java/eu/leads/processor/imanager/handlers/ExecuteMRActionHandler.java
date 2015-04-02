package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;

/**
 * Created by vagvaz on 4/2/15.
 */
public class ExecuteMRActionHandler implements ActionHandler {
   private final Node com;
   private final LogProxy log;
   private final InfinispanManager persistence;
   private final String id;

   public ExecuteMRActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
   }

   @Override
   public Action process(Action action) {
      return null;
   }
}

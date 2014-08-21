package eu.leads.processor.planner;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.RecursiveCallQuery;
import eu.leads.processor.core.plan.SpecialQuery;
import leads.tajo.module.TaJoModule;
import org.vertx.java.core.json.JsonObject;

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
      }
      return result;
   }
}

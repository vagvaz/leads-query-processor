package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.operators.mapreduce.ApatarMapper;
import eu.leads.processor.infinispan.operators.mapreduce.ApatarReducer;
import eu.leads.processor.infinispan.operators.mapreduce.GroupByMapper;
import eu.leads.processor.infinispan.operators.mapreduce.GroupByReducer;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 11/21/14.
 */
public class DemoMapReduceOperator extends MapReduceOperator {
   public DemoMapReduceOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
      super(com,persistence,log,action);
   }

   @Override
   public void init(JsonObject config) {
      super.init(conf);
      setMapper(new ApatarMapper(conf.toString()));
      setReducer(new ApatarReducer(conf.toString()));
      init_statistics(this.getClass().getCanonicalName());
   }

   @Override
   public void execute() {
      super.execute();
   }

   @Override
   public void cleanup() {
      super.cleanup();
   }
}

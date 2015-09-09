package eu.leads.processor.nqe;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.MapReduceJob;
import eu.leads.processor.infinispan.operators.GenericMapReduceOperator;
import eu.leads.processor.infinispan.operators.Operator;
import eu.leads.processor.infinispan.operators.WordCountOperator;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/7/15.
 */
public class MapReduceOperatorFactory {

  public static Operator createOperator(Node com, InfinispanManager persistence, LogProxy log,
      Action action,LeadsStorage storage) {
    JsonObject object = action.getData();
    MapReduceJob job = new MapReduceJob(object);
    String name = job.getName();
    if (name == null) {
      System.err.println("name == null!");
    } else {
      if (name.equals("wordCount")) {
        return new WordCountOperator(com, persistence, log, action);
      } else if (name.equals("generic")) {
        return new GenericMapReduceOperator(com, persistence, log, action,storage);
      }
    }
    return null;
  }
}

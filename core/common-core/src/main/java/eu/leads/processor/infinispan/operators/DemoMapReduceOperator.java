package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.LeadsCombiner;
import eu.leads.processor.infinispan.LeadsReducer;
import eu.leads.processor.infinispan.continuous.WordCountContinuousOperator;
import eu.leads.processor.infinispan.operators.mapreduce.TransformMapper;
import eu.leads.processor.infinispan.operators.mapreduce.TransformReducer;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 11/21/14.
 */
public class DemoMapReduceOperator extends MapReduceOperator {
  public DemoMapReduceOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
  }

  @Override public void init(JsonObject config) {
    super.init(conf);
    setMapper(new TransformMapper(conf.toString()));
    setFederationReducer(new TransformReducer(conf.toString()));
    init_statistics(this.getClass().getCanonicalName());
  }




  @Override public void setupMapCallable() {
    //      init(conf);

    setMapper(new TransformMapper(conf.toString()));
    super.setupMapCallable();
  }

  @Override public void setupReduceLocalCallable() {
    setLocalReducer(new TransformReducer(conf.toString()));
    super.setupReduceLocalCallable();
  }

  @Override public void setupReduceCallable() {
    setFederationReducer(new TransformReducer(conf.toString()));
    super.setupReduceCallable();
  }


  public LeadsCombiner<?, ?> getCombiner() {
    return combiner;
  }

  public void setCombiner(LeadsCombiner<?, ?> combiner) {
    this.combiner = combiner;
  }

  public void setLocalReducer(LeadsReducer<?, ?> localReducer) {
    this.localReducer = localReducer;
  }
  @Override public String getContinuousListenerClass() {
    return null;
  }
}

package eu.leads.processor.infinispan.continuous;

import eu.leads.processor.infinispan.LeadsBaseCallable;
import eu.leads.processor.infinispan.operators.FilterCallableUpdated;

/**
 * Created by vagvaz on 10/25/15.
 */
public class FilterContinuousOperator extends BasicContinuousOperator {
  @Override protected LeadsBaseCallable getCallableInstance(boolean isReduce, boolean islocal) {
    return new FilterCallableUpdated(conf.toString(),output,null);
  }
}

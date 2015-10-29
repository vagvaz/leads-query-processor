package eu.leads.processor.infinispan.continuous;

import eu.leads.processor.infinispan.LeadsBaseCallable;

/**
 * Created by vagvaz on 10/25/15.
 */
public class ScanContinuousOperator extends BasicContinuousOperator {
  @Override protected LeadsBaseCallable getCallableInstance(boolean isReduce, boolean islocal) {
    return null;
  }
}

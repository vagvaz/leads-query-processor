package eu.leads.processor.infinispan.continuous;

import eu.leads.processor.infinispan.LeadsBaseCallable;
import eu.leads.processor.infinispan.operators.ProjectCallableUpdated;

/**
 * Created by vagvaz on 10/25/15.
 */
public class ProjectContinuousOperator extends BasicContinuousOperator {
  @Override protected LeadsBaseCallable getCallableInstance(boolean isReduce, boolean islocal) {
    return new ProjectCallableUpdated(conf.toString(),output);
  }
}

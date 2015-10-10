package eu.leads.processor.infinispan.continuous;

import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.infinispan.LeadsBaseCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by vagvaz on 10/6/15.
 */
public class OperatorRunCallable implements Callable {
  BasicContinuousOperator owner;
  Logger log;
  public OperatorRunCallable(BasicContinuousOperator basicContinuousOperator) {
    this.owner = basicContinuousOperator;
    log = LoggerFactory.getLogger(this.getClass());
  }

  @Override public Object call() throws Exception {
    LeadsBaseCallable callable  = owner.getCallable();
    try {
      for (Object ob : owner.getInputData().entrySet()) {
        Map.Entry entry = (Map.Entry) ob;
        callable.executeOn(entry.getKey(), entry.getValue());
      }
    }catch (Exception e){
      e.printStackTrace();
      PrintUtilities.logStackTrace(log,e.getStackTrace());
    }
    return null;
  }
}

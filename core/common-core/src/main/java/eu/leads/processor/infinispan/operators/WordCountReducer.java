package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsCombiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Apostolos Nydriotis on 2015/06/23.
 */
public class WordCountReducer extends LeadsCombiner<String, Tuple> {

  Logger log;

  public WordCountReducer(JsonObject configuration) {
    super(configuration);
  }

  public WordCountReducer(String configString) {
    super(configString);
  }


  @Override public void initialize() {
    super.initialize();
    log = LoggerFactory.getLogger(WordCountReducer.class);
  }

  @Override
  public void reduce(String reducedKey, Iterator<Tuple> iter, LeadsCollector collector) {
    //    System.out.println(getClass().getName() + ".reduce!");
    int sum = 0;
    ProfileEvent event = new ProfileEvent("WCRComputeSum",log);
    while (true) {
      try {
        Tuple input = iter.next();
        int count = Integer.valueOf(input.getAttribute("count"));
        sum += count;
      }catch(Exception e){
        if(e instanceof NoSuchElementException){
          break;
        }
        e.printStackTrace();
      }
    }
    event.end();
    event.start("WRCOutputResult");
    Tuple output = new Tuple();
    output.setAttribute("count", sum);
    collector.emit(reducedKey, output);
    event.end();
  }

  @Override
  protected void finalizeTask() {
    System.out.println(getClass().getName() + " finished!");
  }
}

package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.continuous.ConcurrentDiskQueue;
import eu.leads.processor.common.continuous.EventTriplet;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.PluginInterface;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by vagvaz on 9/18/15.
 */
public class PluginRunnable implements Runnable{

  private PluginRunnerFilter runnerFilter;
  private Object key;
  private Object value;
  private Cache cache;
  private EventType type;
  private PluginInterface plugin;
  private ProfileEvent event;
  private Logger log;
  private ConcurrentDiskQueue queue;
  private boolean keepRunning = true;

  public PluginRunnable(PluginRunnerFilter filter, PluginInterface pluigin, ConcurrentDiskQueue queue){
    this.runnerFilter = filter;
    this.plugin = pluigin;
    log = LoggerFactory.getLogger(PluginRunnable.class);
    this.queue = queue;
    event = new ProfileEvent("Nothing",log);
  }
  @Override public void run() {
    event.start("PLUGINRunnable: plugin-> " + plugin.getId() + " key " + key.toString());
    while(true && keepRunning){
      if(queue.isEmpty() || plugin == null){
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      else{
        EventTriplet triplet = (EventTriplet) queue.poll();
        switch (triplet.getType()) {
          case CREATED:
            plugin.created(triplet.getKey(),triplet.getValue(),cache);
            break;
          case MODIFIED:
            plugin.modified(triplet.getKey(), triplet.getValue(), cache);
            break;
          case REMOVED:
            plugin.removed(triplet.getKey(), triplet.getValue(), cache);
            break;
        }
        triplet = null;
      }
    }
//    log.error("Rum plugin " + plugin.getId() + " for " + key.toString());
//    try {
//      switch (type) {
//        case CREATED:
//          plugin.created(key, value, cache);
//          break;
//        case MODIFIED:
//          plugin.modified(key, value, cache);
//          break;
//        case REMOVED:
//          plugin.removed(key, value, cache);
//          break;
//      }
//      event.end();
//    }catch (Exception e){
//      e.printStackTrace();
//      PrintUtilities.logStackTrace(log,e.getStackTrace());
//      event.end();
//    }
//    release();
//    runnerFilter.addRunnable(this);

  }



  public void setPlugin(PluginInterface plugin) {
    this.plugin = plugin;
  }

  public void setCache(Cache cache){
    this.cache = cache;
  }

  public void setKeepRunning(boolean keepRunning){
    this.keepRunning = keepRunning;
  }
}

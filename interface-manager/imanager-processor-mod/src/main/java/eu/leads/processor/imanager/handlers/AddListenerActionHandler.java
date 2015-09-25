package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.common.infinispan.*;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/25/15.
 */
public class AddListenerActionHandler implements ActionHandler {
  private Logger log;
  private InfinispanManager imanager;
  public AddListenerActionHandler(Node com, LogProxy logg, InfinispanManager persistence, String id) {
    log = LoggerFactory.getLogger(AddListenerActionHandler.class);
    imanager =persistence;
  }

  @Override public Action process(Action action) {
    Action result = action;
    JsonObject actionResult = new JsonObject();
    actionResult.putString("status","SUCCESS");
    actionResult.putString("message","");
    JsonObject data = action.getData();
    String cache = data.getString("cache");
    String listener = data.getString("listener");
    JsonObject conf = data.getObject("conf");
    try{
      LeadsListener leadsListener = null;
      if(listener.equals("scan")){
        leadsListener =  new ScanCQLListener(conf);
      }else if(listener.equals("topk-1")){
        leadsListener =  new TopkFirstStageListener(conf);
      }else if(listener.equals("topk-2")){
        leadsListener = new TopkSecondStageListener(conf);
      }else if(listener.equals("output")){
        leadsListener = new OutputCQLListener(conf);
      }
      else{
        System.err.println("Listener unknown " + listener);
      }
      if(leadsListener != null) {
        imanager.addListener(leadsListener,cache);
      }
    }catch(Exception e){
      actionResult.putString("status","FAIL");
      actionResult.putString("error",e.getMessage() == null ? "null" : e.getMessage().toString());
    }
    result.setResult(actionResult);
    return result;
  }
}

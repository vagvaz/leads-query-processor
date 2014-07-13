package eu.leads.processor.core.net;

import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/13/14.
 */
public interface Component {

  public boolean setup();
  public boolean startExecution();
  public boolean stopExecution();
  public boolean cleanup();
  public void kill();
  public void processAction(JsonObject message);
  public String getId();
  public void setId(String id);



}

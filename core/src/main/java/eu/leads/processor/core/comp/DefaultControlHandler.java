package eu.leads.processor.core.comp;

import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/13/14.
 */
public class DefaultControlHandler implements LeadsMessageHandler {
  Component owner;
  public DefaultControlHandler(Component owner) {
    this.owner = owner;
  }

  @Override
  public void handle(JsonObject jsonObject) {

  }
}

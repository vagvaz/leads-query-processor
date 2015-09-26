package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.LeadsListener;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/25/15.
 */
public class TopkFirstStageListener implements LeadsListener{
  public TopkFirstStageListener(JsonObject conf) {

  }

  @Override public InfinispanManager getManager() {
    return null;
  }

  @Override public void setManager(InfinispanManager manager) {

  }

  @Override public void initialize(InfinispanManager manager, JsonObject conf) {

  }

  @Override public void initialize(InfinispanManager manager) {

  }

  @Override public String getId() {
    return TopkFirstStageListener.class.toString();
  }

  @Override public void close() {

  }
}

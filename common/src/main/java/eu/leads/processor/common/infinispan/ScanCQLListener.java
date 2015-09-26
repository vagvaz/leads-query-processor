package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.LeadsListener;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/25/15.
 */
public class ScanCQLListener implements LeadsListener {
  public ScanCQLListener(JsonObject conf) {

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
    return ScanCQLListener.class.toString();
  }

  @Override public void close() {

  }
}

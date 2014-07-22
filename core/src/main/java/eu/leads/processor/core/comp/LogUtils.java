package eu.leads.processor.core.comp;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/13/14.
 */
public class LogUtils {
  JsonObject logMessage = new JsonObject();
  EventBus bus;
  String logId;
  public LogUtils() {
  }

  public LogUtils(EventBus eventBus, String logId) {
    bus = eventBus;
    this.logId = logId;
  }

  public  void info(String message) {
    logMessage.putString("type","info");
    logMessage.putString("message",message);
    bus.publish(logId,logMessage);
  }

  public  void warn(String message) {
    logMessage.putString("type","warn");
    logMessage.putString("message",message);
    bus.publish(logId,logMessage);
  }
  public  void error( String message) {
    logMessage.putString("type","error");
    logMessage.putString("message",message);
    bus.publish(logId,logMessage);
  }
  public  void debug( String message) {
    logMessage.putString("type","debug");
    logMessage.putString("message",message);
    bus.publish(logId,logMessage);
  }
  public  void fatal( String message) {
    logMessage.putString("type","fatal");
    logMessage.putString("message",message);
    bus.publish(logId,logMessage);
  }
}

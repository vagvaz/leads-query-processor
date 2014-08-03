package eu.leads.processor.core;

import eu.leads.processor.core.comp.LeadsMessageHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/16/14.
 */
public class ReplyHandler implements LeadsMessageHandler {

  private JsonObject message = null;
  private Object mutex = new Object();

  public JsonObject waitForMessage(){
    JsonObject result = null;
    synchronized ( mutex ){

      while(message == null) {
        try {
          mutex.wait();
        } catch ( InterruptedException e ) {
          e.printStackTrace();
        }
      }
      result = message;
      message = null;
    }
    return result;
  }

  public boolean waitForStatus(){
    boolean result = true;
    synchronized ( mutex ){

      while(message == null) {
        try {
          mutex.wait();
        } catch ( InterruptedException e ) {
          e.printStackTrace();
        }
      }
      result = message.getString("status").equals("ok");
      message = null;
    }
    return result;
  }

    @Override
    public void handle(JsonObject jsonObject) {
        message = jsonObject;
        mutex.notify();
    }
}

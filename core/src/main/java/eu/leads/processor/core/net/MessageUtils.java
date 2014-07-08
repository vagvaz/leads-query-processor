package eu.leads.processor.core.net;

import com.google.common.base.Strings;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/8/14.
 */
public class MessageUtils {
  public static final String FROM = "FROM";
  public static final String TO = "TO";
  public static final String TYPE = "TYPE";
  public static JsonObject createLeadsMessage(JsonObject message,String from){
    return createLeadsMessage(message,from,null,null);
  }
  public static JsonObject createLeadsMessage(JsonObject message,String from, String to){
    return createLeadsMessage(message,from,to,null);
  }
  public static JsonObject createLeadsMessage(JsonObject message,String from, String to,String type){
    JsonObject result = message;
    if( !Strings.isNullOrEmpty(from)){
      result.putString(FROM,from);
    }
    if( !Strings.isNullOrEmpty(to)){
      result.putString(TO,to);
    }
    if(!Strings.isNullOrEmpty(type)){
      result.putString(TYPE,type);
    }
    return result;
  }


}

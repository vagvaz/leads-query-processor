package eu.leads.processor.core.plan;

import eu.leads.processor.core.DataType;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/4/14.
 */
public class QueryStatus extends DataType {

   public QueryStatus(JsonObject status) {
      super(status);
   }

   public QueryStatus(String id, QueryState state, String s) {
      super();
      setId(id);
      setState(state);
      setErrorMessage(s);
   }

   public QueryStatus() {
      super();
      setId("");
      setState(QueryState.PENDING);
      setErrorMessage("");
   }

   public void setId(String id) {
      data.putString("id", id);
   }

   public String getId() {
      return data.getString("id");
   }

   public QueryState getState() {
      return QueryState.valueOf(data.getString("state"));
   }
   public String getErrorMessage(){
      return data.getString("error-message");
   }

   public void setState(QueryState state){
      data.putString("state",state.toString());
   }

   public void setErrorMessage(String message){
      data.putString("error-message",message);
   }
}

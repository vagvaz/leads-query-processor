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
      setStatus(state);
      setErrorMessage(s);
   }

   public QueryStatus() {
      super();
      setId("");
      setStatus(QueryState.PENDING);
      setErrorMessage("");
   }

   public void setId(String id) {
      data.putString("id", id);
   }

   public String getId() {
      return data.getString("id");
   }

   public QueryState getStatus() {
      return QueryState.valueOf(data.getString("status"));
   }
   public String getErrorMessage(){
      return data.getString("errorMessage");
   }

   public void setStatus(QueryState state){
      data.putString("status",state.toString());
   }

   public void setErrorMessage(String message){
      data.putString("errorMessage",message);
   }
}

package eu.leads.processor.web;

import com.google.common.base.Strings;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.imanager.IManagerConstants;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.UUID;

/**
 * Created by vagvaz on 8/4/14.
 */
public class GetQueryStatusHandler implements Handler<HttpServerRequest> {
   Node com;
   LogProxy log;
   Map<String,GetQueryStatusReplyHandler> replyHandlers;


   private class GetQueryStatusReplyHandler implements LeadsMessageHandler {
      HttpServerRequest request;
      String requestId;
      public GetQueryStatusReplyHandler(String requestId, HttpServerRequest request){
         this.request = request;
         this.requestId = requestId;
      }
      @Override
      public void handle(JsonObject message) {
         if(message.containsField("error")){
            replyForError(message);
            return;
         }
         message.removeField(MessageUtils.FROM);
         message.removeField(MessageUtils.TO);
         message.removeField(MessageUtils.COMTYPE);
         request.response().end(message.toString());
         cleanup(requestId);
      }

      private void replyForError(JsonObject message) {
         if(message != null ){
            log.error(message.getString("message"));
            request.response().end("{}");
         }
         else{
            log.error("Request for Query Status had empty query Id.");
            request.response().setStatusCode(400);
         }
         cleanup(requestId);
      }
   }



   public GetQueryStatusHandler(final Node com,LogProxy log) {
      this.com = com;
      this.log = log;
   }

   @Override
   public void handle(HttpServerRequest request) {
      request.response().setStatusCode(200);
      request.response().putHeader(WebStrings.CONTENT_TYPE,WebStrings.APP_JSON);
      log.info("Put Object Request");
      String reqId = UUID.randomUUID().toString();
      GetQueryStatusReplyHandler replyHandler = new GetQueryStatusReplyHandler(reqId,request);

      String queryId = request.params().get("id");
      if(Strings.isNullOrEmpty(queryId)){
         replyHandler.replyForError(null);
         return;
      }
      Action action = new Action();
      action.setId(reqId);
      action.setCategory(StringConstants.ACTION);
      action.setLabel(IManagerConstants.GET_QUERY_STATUS);
      action.setOwnerId(com.getId());
      action.setComponentType("webservice");
      action.setTriggered("");
      action.setTriggers(new JsonArray());

      JsonObject  queryRequest = new JsonObject();
      queryRequest.putString("queryId",queryId);
      action.setData(queryRequest);
      com.sendRequestTo(StringConstants.IMANAGERQUEUE,action.asJsonObject(),replyHandler);
      replyHandlers.put(action.getId(),replyHandler);
   }

   public void cleanup(String id){
      GetQueryStatusReplyHandler rh = replyHandlers.remove(id);
      rh = null;
   }
}

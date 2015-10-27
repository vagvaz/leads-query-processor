package eu.leads.processor.web;

import com.google.common.base.Strings;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.imanager.IManagerConstants;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by vagvaz on 10/26/15.
 */
public class GetWebResultsHandler implements Handler<HttpServerRequest> {

  Node com;
  Logger log;
  Map<String, GetWebResultsReplyHandler> replyHandlers;
  static String prefix = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\"><html class=\"js\" xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" version=\"XHTML+RDFa 1.0\" dir=\"ltr\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:dc=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:og=\"http://ogp.me/ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:sioc=\"http://rdfs.org/sioc/ns#\" xmlns:sioct=\"http://rdfs.org/sioc/types#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><head profile=\"http://www.w3.org/1999/xhtml/vocab\">\n"
      + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
      + "<style type=\"text/css\" media=\"all\">\n" + "<!--/*--><![CDATA[/*><!--*/\n"
      + "#page { width: 100%;  text-align:center; background-color:#C0C0C0}\n"
      + "body.sidebar-first #main, body.two-sidebars #main { margin-left: -210px !important; margin-right: 0px;}\n"
      + "body.sidebar-first #squeeze, body.two-sidebars #squeeze { margin-left: 210px !important; margin-right: 0px; }\n"
      + "#sidebar-left { width: 210px; text-align:center;}\n"
      + "body.sidebar-second #main, body.two-sidebars #main { margin-right: -210px !important; margin-left: 0px;}\n"
      + "body.sidebar-second #squeeze, body.two-sidebars #squeeze { margin-right: 210px !important; margin-left: 0px; }\n"
      + "#sidebar-right { width:100%; text-align:center; height:1000px; background-color: #F0F0F0;}\n"
      + "/* #foo {width:100%;height:auto; background-color:red;} */\n"
      + "body { font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; text-align:center align:center; }\n"
      + "img#logo { margin-right: -210px !important; margin-left: 0px; margin-leftwidth: 580px; height: 92px; }\n"
      + "\n" + "</style></head>\n" + "<body>\n" + "<div id=\"page\">\n" + "<div id=sidebar-left>\n" + "</div>\n"
      + "<div id=\"logo-title\">\n" + "      <!-- logo -->\n" + "        <img src=\"banner.png\" id=\"logo\">\n"
      + "      <!-- /logo -->\n" + "      </div>\n" + "      <div>\n" + "     \n" + "      </div>\n"
      + "  <div id = \"sidebar-right\">" ;
  static String suffix = "</div>\n" + "</div>\n" + "    \n" + "</body>\n" + "</html>";

  public GetWebResultsHandler(final Node com, Logger log) {
    this.com = com;
    this.log = log;
    replyHandlers = new HashMap<>();
  }

  @Override
  public void handle(HttpServerRequest request) {
    request.response().setStatusCode(200);
    request.response().putHeader(WebStrings.CONTENT_TYPE, WebStrings.APP_JSON);
    log.info("Put Object Request");
    String reqId = UUID.randomUUID().toString();
    GetWebResultsReplyHandler replyHandler = new GetWebResultsReplyHandler(reqId, request);
    JsonObject queryRequest = new JsonObject();
    queryRequest.putString("type", "getResults");
    String queryId = request.params().get("id");
//    String min = request.params().get("min");
//    String max = request.params().get("max");
    if (Strings.isNullOrEmpty(queryId)) {
      replyHandler.replyForError(null);
      return;
    }

    queryRequest.putString("queryId", queryId);
//    queryRequest.putString("min", min);
//    queryRequest.putString("max", max);


    Action action = new Action();
    action.setId(reqId);
    action.setCategory(StringConstants.ACTION);
    action.setLabel(IManagerConstants.GET_WEB_RESULTS);
    action.setOwnerId(com.getId());
    action.setComponentType("webservice");
    action.setTriggered("");
    action.setTriggers(new JsonArray());
    action.setDestination(StringConstants.IMANAGERQUEUE);
    action.setStatus(ActionStatus.PENDING.toString());
    action.setData(queryRequest);

    com.sendRequestTo(StringConstants.IMANAGERQUEUE, action.asJsonObject(), replyHandler);
    replyHandlers.put(reqId, replyHandler);
  }

  public void cleanup(String id) {
    GetWebResultsReplyHandler rh = replyHandlers.remove(id);
    rh = null;
  }


  private class GetWebResultsReplyHandler implements LeadsMessageHandler {
    HttpServerRequest request;
    String requestId;

    public GetWebResultsReplyHandler(String requestId, HttpServerRequest request) {
      this.request = request;
      this.requestId = requestId;
    }

    @Override
    public void handle(JsonObject message) {
      if (message.containsField("error")) {
        replyForError(message);
        return;
      }
      log.info("GetWebResults webservice received reply " + message.getString(MessageUtils.TO) + " " + message.getValue(MessageUtils.MSGID).toString());
      message.removeField(MessageUtils.FROM);
      message.removeField(MessageUtils.TO);
      message.removeField(MessageUtils.COMTYPE);
      message.removeField(MessageUtils.MSGID);
      message.removeField(MessageUtils.MSGTYPE);
      //            log.info("end requests");
      String mainBody = getMainBody(message.toString());
      request.response().end(prefix+mainBody+suffix);
      cleanup(requestId);
    }

    private String getMainBody(String s) {
      JsonObject object = new JsonObject(s);
      return object.encodePrettily();
    }

    private void replyForError(JsonObject message) {
      if (message != null) {
        log.error(message.getString("message"));
        request.response().end("{}");
      } else {
        log.error("Request for Query Status had empty query Id.");
        request.response().setStatusCode(400);
      }
      cleanup(requestId);
    }
  }

  public static void main(String[] args) {
   System.out.println(prefix+suffix);
  }
}


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
 * Created by vagvaz on 8/4/14.
 */
public class UndeployPluginHandler implements Handler<HttpServerRequest> {
    Node com;
    Logger log;
    Map<String, GetQueryStatusReplyHandler> replyHandlers;


    public UndeployPluginHandler(final Node com, Logger log) {
        this.com = com;
        this.log = log;
        replyHandlers = new HashMap<>();

    }

    @Override
    public void handle(HttpServerRequest request) {
        request.response().setStatusCode(200);
        request.response().putHeader(WebStrings.CONTENT_TYPE, WebStrings.APP_JSON);
        log.info("Get Query Results Request");
        String reqId = UUID.randomUUID().toString();
        GetQueryStatusReplyHandler replyHandler = new GetQueryStatusReplyHandler(reqId, request);


        JsonObject deployRequest = new JsonObject();
        String pluginname = request.params().get("pluginname");
        String cachename = request.params().get("cachename");

        if (Strings.isNullOrEmpty(pluginname) || pluginname.equals("{}")
                || Strings.isNullOrEmpty(cachename) || cachename.equals("{}")) {
            replyHandler.replyForError(null);
            return;
        }
        deployRequest.putString("pluginname", pluginname);
        deployRequest.putString("cachename", cachename);

        Action action = new Action();
        action.setId(reqId);
        action.setCategory(StringConstants.ACTION);
        action.setLabel(IManagerConstants.UNDEPLOY_PLUGIN);
        action.setOwnerId(com.getId());
        action.setComponentType("webservice");
        action.setTriggered("");
        action.setTriggers(new JsonArray());
        action.setStatus(ActionStatus.PENDING.toString());
        action.setData(deployRequest);
        com.sendRequestTo(StringConstants.IMANAGERQUEUE, action.asJsonObject(), replyHandler);
        replyHandlers.put(action.getId(), replyHandler);
    }

    public void cleanup(String id) {
        GetQueryStatusReplyHandler rh = replyHandlers.remove(id);
        rh = null;
    }


    private class GetQueryStatusReplyHandler implements LeadsMessageHandler {
        HttpServerRequest request;
        String requestId;

        public GetQueryStatusReplyHandler(String requestId, HttpServerRequest request) {
            this.request = request;
            this.requestId = requestId;
        }

        @Override
        public void handle(JsonObject message) {
            if (message.containsField("error")) {
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
}

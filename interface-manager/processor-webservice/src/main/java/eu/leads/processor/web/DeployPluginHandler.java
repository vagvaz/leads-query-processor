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
import org.vertx.java.core.buffer.Buffer;
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
public class DeployPluginHandler implements Handler<HttpServerRequest> {

    Node com;
    Logger log;
    Map<String, SubmitDataBodyHandler> bodyHandlers;
    Map<String, SubmitDataReplyHandler> replyHandlers;

    public DeployPluginHandler(final Node com, Logger log) {
        this.com = com;
        this.log = log;
        replyHandlers = new HashMap<>();
        bodyHandlers = new HashMap<>();
    }

    @Override
    public void handle(HttpServerRequest request) {

        System.out.println("request received : " + request.headers().entries().toString());

        //System.out.println("query received : " + request.);
        request.response().setStatusCode(200);
        request.response().putHeader(WebStrings.CONTENT_TYPE, WebStrings.APP_JSON);

        log.info("DeployPlugin Submit Data ");
        String reqId = UUID.randomUUID().toString();
        SubmitDataReplyHandler replyHandler = new SubmitDataReplyHandler(reqId, request);
        SubmitDataBodyHandler bodyHanlder = new SubmitDataBodyHandler(reqId, replyHandler, request);
        replyHandlers.put(reqId, replyHandler);
        bodyHandlers.put(reqId, bodyHanlder);
        request.bodyHandler(bodyHanlder);
    }

    public void cleanup(String id) {
        SubmitDataReplyHandler rh = replyHandlers.remove(id);
        SubmitDataBodyHandler bh = bodyHandlers.remove(id);
        rh = null;
        bh = null;
    }

    private class SubmitDataReplyHandler implements LeadsMessageHandler {
        HttpServerRequest request;
        String requestId;

        public SubmitDataReplyHandler(String requestId, HttpServerRequest request) {
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
                log.error("No Data to submit");
                request.response().setStatusCode(400);
            }
            cleanup(requestId);
        }
    }


    private class SubmitDataBodyHandler implements Handler<Buffer> {


        private final SubmitDataReplyHandler replyHandler;
        private final String requestId;
        private final HttpServerRequest request;

        public SubmitDataBodyHandler(String requestId, SubmitDataReplyHandler replyHandler, HttpServerRequest request) {
            this.replyHandler = replyHandler;
            this.requestId = requestId;
            this.request = request;
        }

        @Override
        public void handle(Buffer body) {
            // String data = body..getString(0, body.length());
            String Request = body.getString(0, body.length());

            JsonObject deployRequest = new JsonObject(Request);
            String pluginname = request.params().get("pluginname");
            String cachename = request.params().get("cachename");

            if (Strings.isNullOrEmpty(Request) || Request.equals("{}")
                    || Strings.isNullOrEmpty(pluginname) || pluginname.equals("{}")
                    || Strings.isNullOrEmpty(cachename) || cachename.equals("{}")) {
                replyHandler.replyForError(null);
                return;
            }
            deployRequest.putString("pluginname", pluginname);
            deployRequest.putString("cachename", cachename);

            Action action = new Action();
            action.setId(requestId);

            action.setCategory(StringConstants.ACTION);
            action.setLabel(IManagerConstants.DEPLOY_PLUGIN);
            action.setOwnerId(com.getId());
            action.setComponentType("webservice");
            action.setTriggered("");
            action.setTriggers(new JsonArray());
            action.setData(deployRequest);
            System.out.println("Deploy plugin: " + deployRequest.toString());
            action.setDestination(StringConstants.IMANAGERQUEUE);
            action.setStatus(ActionStatus.PENDING.toString());
            com.sendRequestTo(StringConstants.IMANAGERQUEUE, action.asJsonObject(), replyHandler);
        }
    }
}

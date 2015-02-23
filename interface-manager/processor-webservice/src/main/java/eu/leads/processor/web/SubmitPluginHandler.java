package eu.leads.processor.web;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.imanager.IManagerConstants;
import eu.leads.processor.common.plugins.PluginPackage;
import org.apache.commons.lang.SerializationUtils;
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
public class SubmitPluginHandler implements Handler<HttpServerRequest> {

    Node com;
    Logger log;
    Map<String, SubmitPluginBodyHandler> bodyHandlers;
    Map<String, SubmitPluginReplyHandler> replyHandlers;

    public SubmitPluginHandler(final Node com, Logger log) {
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

        log.info("Submit Data ");
        String reqId = UUID.randomUUID().toString();
        SubmitPluginReplyHandler replyHandler = new SubmitPluginReplyHandler(reqId, request);
        SubmitPluginBodyHandler bodyHanlder = new SubmitPluginBodyHandler(reqId, replyHandler);
        replyHandlers.put(reqId, replyHandler);
        bodyHandlers.put(reqId, bodyHanlder);
        request.bodyHandler(bodyHanlder);
    }

    public void cleanup(String id) {
        SubmitPluginReplyHandler rh = replyHandlers.remove(id);
        SubmitPluginBodyHandler bh = bodyHandlers.remove(id);
        rh = null;
        bh = null;
    }

    private class SubmitPluginReplyHandler implements LeadsMessageHandler {
        HttpServerRequest request;
        String requestId;

        public SubmitPluginReplyHandler(String requestId, HttpServerRequest request) {
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
            message.removeField(MessageUtils.MSGID);
            message.removeField(MessageUtils.MSGTYPE);
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


    private class SubmitPluginBodyHandler implements Handler<Buffer> {


        private final SubmitPluginReplyHandler replyHandler;
        private final String requestId;

        public SubmitPluginBodyHandler(String requestId, SubmitPluginReplyHandler replyHandler) {
            this.replyHandler = replyHandler;
            this.requestId = requestId;
        }

        @Override
        public void handle(Buffer body) {
            // String data = body..getString(0, body.length());
            System.out.println(" handle 4");
            int endbyte = 4;

            byte[] receivedData = body.getBytes() ;
            int numofbytes = body.getBytes().length;
            if (numofbytes <=0) {
                replyHandler.replyForError(null);
            }
            System.out.println("Data receivedfull_: " + numofbytes);

            System.out.println("Data received1: " + receivedData.length + " SizeofString byte: " + endbyte);
//
            PluginPackage pluginRegister = (PluginPackage) SerializationUtils.deserialize(receivedData);

            System.out.print("Received size of jar: " + pluginRegister.getJar().length);
            receivedData = null;
            System.gc();
            pluginRegister.putBinary("jar",pluginRegister.getJar());
            pluginRegister.setJar(null);
            System.gc();

            pluginRegister.putString("classname",pluginRegister.getClassName());
            pluginRegister.putBinary("config",pluginRegister.getConfig());
            pluginRegister.putString("id",pluginRegister.getId());
            Action action = new Action();
            action.setId(requestId);
            action.setCategory(StringConstants.ACTION);
            action.setLabel(IManagerConstants.SUBMIT_PLUGIN);
            action.setOwnerId(com.getId());
            action.setComponentType("webservice");
            action.setTriggered("");
            action.setTriggers(new JsonArray());
            action.setData(pluginRegister);
             System.out.println("Plugin received: " + pluginRegister.toString() + " plugin: " +
                      pluginRegister.getClassName()+ "  " + pluginRegister.getId() );
            action.setDestination(StringConstants.IMANAGERQUEUE);
            action.setStatus(ActionStatus.PENDING.toString());
            System.out.println(action.toString());
            com.sendRequestTo(StringConstants.IMANAGERQUEUE, action.asJsonObject(), replyHandler);
        }
    }
}

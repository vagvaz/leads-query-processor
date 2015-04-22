package eu.leads.processor.web;

import com.google.common.base.Strings;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by angelos on 24/03/15.
 */
public class MonitorQueryResultsHandler implements Handler<HttpServerRequest> {



    Node com;
    Logger log;
    Map<String, MonitorQueryResultsBodyHandler> bodyHandlers;
    Map<String, MonitorQueryResultsReplyHandler> replyHandlers;


    public MonitorQueryResultsHandler(final Node com, Logger log) {
        log.info("...MonitorQueryHandler");
        this.com = com;
        this.log = log;
        replyHandlers = new HashMap<>();
        bodyHandlers = new HashMap<>();
    }

    @Override
    public void handle(HttpServerRequest request) {
        log.info("handle MonitorQueryHandler");
        request.response().setStatusCode(200);
        request.response().putHeader(WebStrings.CONTENT_TYPE, WebStrings.APP_JSON);
        log.info("MonitorQueryHandler Request");
        String reqId = UUID.randomUUID().toString();
        log.info("reqId: "+reqId);

        MonitorQueryResultsReplyHandler replyHandler = new MonitorQueryResultsReplyHandler(reqId, request);
        log.info("replyHandler: "+replyHandler);

        MonitorQueryResultsBodyHandler bodyHanlder = new MonitorQueryResultsBodyHandler(reqId, replyHandler);
        log.info("bodyHanlder: "+bodyHanlder);

        replyHandlers.put(reqId, replyHandler);
        log.info("replyHandler: "+replyHandlers.get(reqId));

        bodyHandlers.put(reqId, bodyHanlder);
        log.info("bodyHandlers: "+bodyHandlers.get(reqId));

        request.bodyHandler(bodyHanlder);
        log.info("Make a request");
    }

    public void cleanup(String id) {
        log.info("cleanup MonitorQueryHandler");
        MonitorQueryResultsReplyHandler rh = replyHandlers.remove(id);
        MonitorQueryResultsBodyHandler bh = bodyHandlers.remove(id);
        rh = null;
        bh = null;
    }


    private class MonitorQueryResultsReplyHandler implements LeadsMessageHandler {
        HttpServerRequest request;
        String requestId;

        public MonitorQueryResultsReplyHandler(String requestId, HttpServerRequest request) {
            log.info("MonitorQueryReplyHandler: "+requestId+","+request);
            this.request = request;
            this.requestId = requestId;
        }

        @Override
        public void handle(JsonObject message) {
            log.info("MonitorQueryResultsReplyHandler handle message: "+message);
            if (message.containsField("error")) {
                replyForError(message);
            }
            log.info("FROM:"+MessageUtils.FROM);
            log.info("TO:" + MessageUtils.TO);
            log.info("COMTYPE:" + MessageUtils.COMTYPE);
            log.info("MSGID:" + MessageUtils.MSGID);
            log.info("MSGTYPE:" + MessageUtils.MSGTYPE);
            message.removeField(MessageUtils.FROM);
            message.removeField(MessageUtils.TO);
            message.removeField(MessageUtils.COMTYPE);
            message.removeField(MessageUtils.MSGID);
            message.removeField(MessageUtils.MSGTYPE);
            request.response().end(message.toString());
            cleanup(requestId);
        }

        private void replyForError(JsonObject message) {
            log.info("MonitorQueryReplyHandler replyForError message: "+message);
            if (message != null) {
                log.error(message.getString("message"));
                request.response().end("{}");
            } else {
                log.error("Empty Request");
                request.response().setStatusCode(400);
            }
            cleanup(requestId);
        }
    }


    private class MonitorQueryResultsBodyHandler implements Handler<Buffer> {


        private final MonitorQueryResultsReplyHandler replyHandler;
        private final String requestId;

        public MonitorQueryResultsBodyHandler(String requestId, MonitorQueryResultsReplyHandler replyHandler) {
            this.replyHandler = replyHandler;
            this.requestId = requestId;
        }

        @Override
        public void handle(Buffer body) {
            String query = body.getString(0, body.length());
            if (Strings.isNullOrEmpty(query) || query.equals("{}")) {
                replyHandler.replyForError(null);
                return;
            }

//            Action action = new Action();
//            action.setId(requestId);
//            action.setCategory(StringConstants.ACTION);
//            action.setLabel(IManagerConstants.MONITOR_QUERY_RESULTS);
//            action.setOwnerId(com.getId());
//            action.setComponentType("webservice");
//            action.setTriggered("");
//            action.setTriggers(new JsonArray());
//            JsonObject object = new JsonObject(query);
//            action.setData(object);
//            action.setDestination(StringConstants.IMANAGERQUEUE);
//            action.setStatus(ActionStatus.PENDING.toString());
//            com.sendRequestTo(StringConstants.IMANAGERQUEUE, action.asJsonObject(), replyHandler);

            JsonObject job  = new JsonObject();
            job.putString("status","SUCCESS");
            job.putString("message","aa");
            replyHandler.handle(job);
        }
    }
}
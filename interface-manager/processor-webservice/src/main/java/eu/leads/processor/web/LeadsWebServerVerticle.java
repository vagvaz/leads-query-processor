package eu.leads.processor.web;

import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/**
 * Created by vagvaz on 8/3/14.
 */
public class LeadsWebServerVerticle extends Verticle implements LeadsMessageHandler {

    Handler<HttpServerRequest> failHandler;
    Node com;
    Logger log;
    String id;

    public void start() {
        id = "webservice-" + LQPConfiguration.getInstance().getHostname() + container.config()
                                                                                .getNumber("port",
                                                                                              8080)
                                                                                .toString();
        LQPConfiguration.initialize(true);
        container.logger().info("Leads Processor REST service is starting..");
        com = new DefaultNode();

        log = container.logger();
        com.initialize(id, "webservice", null, this, null, getVertx());
        JsonObject config = container.config();
        RouteMatcher matcher = new RouteMatcher();

        //      startEmbeddedCacheManager(configFile);
        //      startLeadsWebModule("propertiesFile");
        GetObjectHandler getObjectHandler = new GetObjectHandler(com, log);
        PutObjectHandler putObjectHandler = new PutObjectHandler(com, log);
        GetQueryStatusHandler getQueryStatusHandler = new GetQueryStatusHandler(com, log);
        GetResultsHandler getResultsHandler = new GetResultsHandler(com, log);
        Handler<HttpServerRequest> submitWorkflowHandler = new SubmitWorkflowHandler(com, log);
        Handler<HttpServerRequest> submitDataHandler = new SubmitPluginHandler(com, log);

        Handler<HttpServerRequest> deployPluginHandler  = new DeployPluginHandler(com, log);
        Handler<HttpServerRequest> undeployPluginHandler = new UndeployPluginHandler(com, log);

        SubmitQueryHandler submitQueryHandler = new SubmitQueryHandler(com, log);
        SubmitSpecialCallHandler submitSpecialCallHandler = new SubmitSpecialCallHandler(com, log);

        //object
        failHandler = new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                JsonObject object = new JsonObject();
                request.response().setStatusCode(400);
                request.response().putHeader(WebStrings.CONTENT_TYPE, WebStrings.APP_JSON);
                object.putString("status", "failed");
                object.putString("message", "Invalid requst path");
                request.response().end(object.toString());
            }
        };

        matcher.noMatch(failHandler);
        matcher.post("/rest/object/get/", getObjectHandler);
        matcher.post("/rest/object/put/", putObjectHandler);
        //
        //      //query   [a-zA-Z0-9]+\-[a-zA-Z0-9]+\-[a-zA-Z0-9]+\-[a-zA-Z0-9]+\-[a-zA-Z0-9]+
        matcher.get("/rest/query/status/:id", getQueryStatusHandler);
        //      //id:[a-zA-Z0-9]+@[a-zA-Z0-9]+}/{min:[0-9]+}/{max:[0-9]+}
        matcher.get("/rest/query/results/:id/min/:min/max/:max", getResultsHandler);
        matcher.post("/rest/query/submit", submitQueryHandler);
        matcher.post("/rest/workflow/submit", submitWorkflowHandler);
        matcher.post("/rest/data/submit", submitDataHandler);
        matcher.post("/rest/query/wgs/:type", submitSpecialCallHandler);
        //

        matcher.post("/rest/deploy/plugin/:pluginname/:cachename", deployPluginHandler);
        matcher.get("/rest/undeploy/plugin/:pluginname/:cachename", undeployPluginHandler);


        matcher.get("/rest/checkOnline", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest httpServerRequest) {
                httpServerRequest.response().setStatusCode(200);
                httpServerRequest.response()
                    .putHeader(WebStrings.CONTENT_TYPE, WebStrings.TEXT_HTML);
                httpServerRequest.response()
                    .end("<html><h1>Leads Query Processor REST Service</h1> <p>Yes I am online</p></html>");

            }
        });
        vertx.createHttpServer().requestHandler(matcher)
            .listen((Integer) config.getNumber("port", 8080));

        container.logger().info("Webserver started");
    }

    @Override
    public void handle(JsonObject message) {
        System.out.println(id + " received message " + message.toString());
        log.warn(id + " received message " + message.toString());
    }
}

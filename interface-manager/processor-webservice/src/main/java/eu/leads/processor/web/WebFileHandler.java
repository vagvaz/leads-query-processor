package eu.leads.processor.web;

import eu.leads.processor.core.net.Node;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;

/**
 * Created by vagvaz on 10/28/15.
 */
public class WebFileHandler implements Handler<HttpServerRequest> {
  public WebFileHandler(Node com, Logger log) {
  }

  @Override public void handle(HttpServerRequest httpServerRequest) {
    String file = httpServerRequest.params().get("file");
    httpServerRequest.response().sendFile(file);
  }
}

package eu.leads.processor.nqe.operators;


import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

public interface Operator {
    public JsonObject getConfiguration();
    public void init(JsonObject config);
    public void execute();
    public void cleanup();

    public void setConfiguration(JsonObject config);
}

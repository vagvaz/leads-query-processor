package eu.leads.processor.nqe.operators;

import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

/**
 * Created by tr on 30/8/2014.
 */
public abstract class BasicOperator implements Operator{
    JsonObject conf;
    OperatorType operatorType;

    @Override
    public JsonObject getConfiguration() {
        return conf;
    }

    @Override
    public void setConfiguration(JsonObject config) {
        conf = config;
    }
}

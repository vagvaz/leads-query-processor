package eu.leads.processor.nqe.operators;


import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

public interface Operator{
    public JsonObject getConfiguration();
    public void init(JsonObject config);
    public void execute();
    public void cleanup();
    public void setConfiguration(JsonObject config);
    public String getInput();
    public void setInput(String input);
    public String getOutput();
    public void setOutput(String output);
    public JsonObject getOperatorParameters();
    public void setOperatorParameters(JsonObject parameters);

}

package eu.leads.processor.nqe.operators;


import java.util.Properties;

public interface Operator {
    public Properties getConfiguration();

    public void setConfiguration(Properties config);
}

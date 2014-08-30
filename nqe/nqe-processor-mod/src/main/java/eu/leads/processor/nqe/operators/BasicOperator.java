package eu.leads.processor.nqe.operators;

import java.util.Properties;

/**
 * Created by tr on 30/8/2014.
 */
public abstract class BasicOperator implements Operator{
    Properties conf;
    OperatorType operatorType;

    @Override
    public Properties getConfiguration() {
        return conf;
    }

    @Override
    public void setConfiguration(Properties config) {
        conf = config;
    }
}

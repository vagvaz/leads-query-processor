package eu.leads.processor.common.test;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.HashMap;

/**
 * Created by vagvaz on 6/3/14.
 */
public class ComplexType {
    private String foo;
    private HashMap<String, Integer> m;
    private XMLConfiguration config;

    public ComplexType(String a) {
        System.out.println("Complex Type initialized");
        foo = a;
        m = new HashMap<String, Integer>();
        m.put(foo, 3);
        try {
            config = new XMLConfiguration("/home/vagvaz/jgroups-tcp.xml");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

    public XMLConfiguration getConfig() {
        return config;
    }

    public void setConfig(XMLConfiguration config) {
        this.config = config;
    }

    public HashMap<String, Integer> getM() {
        return m;
    }

    public void setM(HashMap<String, Integer> m) {
        this.m = m;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}

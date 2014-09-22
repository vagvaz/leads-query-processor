package eu.leads.processor.common.test;


import java.util.Iterator;
import java.util.Properties;

import eu.leads.processor.common.LeadsReducer;

import java.util.Iterator;
import java.util.Properties;

public class WordCountReducer extends LeadsReducer<String, Integer> {

    private static final long serialVersionUID = 1901016598354633256L;

    public WordCountReducer(Properties configuration) {
        super(configuration);

    }

    public Integer reduce(String key, Iterator<Integer> iter) {
        int sum = 0;
        while (iter.hasNext()) {
            Integer i = iter.next();
            sum += i;
        }
        return sum;
    }
}

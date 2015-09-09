import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsReducer;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.Iterator;

/**
 * Created by vagvaz on 3/31/15.
 */
public class TestGenericReducer extends LeadsReducer<String, Tuple> {
    public TestGenericReducer(String configString) {
        super(configString);
    }

    @Override public void reduce(String key, Iterator<Tuple> iterator, LeadsCollector collector) {
        int count = 0;
        Tuple result = new Tuple();
        while (iterator.hasNext()) {
            count++;
            result = new Tuple(iterator.next());
        }

        result.setAttribute("count", count);
        collector.emit(key, result);

    }

    public TestGenericReducer() {
        super();
    }

    @Override public void initialize(XMLConfiguration reduceConfig) {
        super.initialize(reduceConfig);
    }
}

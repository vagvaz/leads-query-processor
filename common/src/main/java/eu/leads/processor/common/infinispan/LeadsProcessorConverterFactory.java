package eu.leads.processor.common.infinispan;

import org.infinispan.filter.Converter;
import org.infinispan.server.hotrod.event.ConverterFactory;
//import org.infinispan.filter.ConverterFactory;


/**
 * Created by vagvaz on 9/29/14.
 */
public class LeadsProcessorConverterFactory implements ConverterFactory {



    @Override public <K, V, C> Converter<K, V, C> getConverter(Object[] objects) {
        return null;
    }
}

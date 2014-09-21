package eu.leads.processor.common.infinispan;

import org.infinispan.filter.KeyValueFilter;
import org.infinispan.metadata.Metadata;

/**
 * Created by vagvaz on 8/13/14.
 */
public class RangeFilter implements KeyValueFilter {
    Long min;
    Long max;

    public RangeFilter(Long min, Long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean accept(Object key, Object value, Metadata metadata) {
        Long longKey = Long.parseLong(key.toString());
        if (min <= longKey && longKey <= max)
            return true;
        return false;
    }
}

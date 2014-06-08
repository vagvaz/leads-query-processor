package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;

/**
 * Created by vagvaz on 6/3/14.
 */
public interface PluginInterface {
    public String getId();

    public void setId(String id);

    public String getClassName();

    public void initialize(Configuration config, InfinispanManager manager);

    public void cleanup();

    public void modified(Object key, Object value, Cache<Object, Object> cache);

    public void created(Object key, Object value, Cache<Object, Object> cache);

    public void removed(Object key, Object value, Cache<Object, Object> cache);

    public Configuration getConfiguration();

    public void setConfiguration(Configuration config);
}

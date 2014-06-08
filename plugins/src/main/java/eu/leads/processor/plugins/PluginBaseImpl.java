package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagvaz on 6/3/14.
 */
public class PluginBaseImpl implements PluginInterface {
    Configuration config;
    Logger log = LoggerFactory.getLogger(this.getClass());
    String id = this.getClass().getCanonicalName();
    InfinispanManager manager;
    Cache logCache;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getClassName() {
        return PluginBaseImpl.class.getCanonicalName();
    }

    @Override
    public void initialize(Configuration config, InfinispanManager manager) {
        this.manager = manager;
        this.config = config;
        log.info("Initiliazed base plugin " + getId());
        log.info("plugin.id " + this.config.getProperty("plugin.id"));
        log.info("plugin.version " + this.config.getProperty("plugin.version"));
        logCache = (Cache) manager.getPersisentCache("logCache");
    }

    @Override
    public void cleanup() {
        log.info("Cleanup " + getId());
    }

    @Override
    public void modified(Object key, Object value, Cache<Object, Object> cache) {
        log.info(id + " modify from " + cache.getName() + " key: " + key.toString() + " --> \nvalue:\n" + value.toString());
        logCache.put(key.toString(), id + " modify from " + cache.getName() + " key: " + key.toString() + " --> \nvalue:\n" + value.toString());
    }

    @Override
    public void created(Object key, Object value, Cache<Object, Object> cache) {
        log.info(id + " create from " + cache.getName() + " key: " + key + " --> \nvalue:\n" + value.toString());
        logCache.put(key.toString(), id + " modify from " + cache.getName() + " key: " + key.toString() + " --> \nvalue:\n" + value.toString());
    }

    @Override
    public void removed(Object key, Object value, Cache<Object, Object> cache) {
        log.info(id + " remove from " + cache.getName() + " key: " + key + " --> \nvalue:\n" + value.toString());
        logCache.put(key.toString(), id + " modify from " + cache.getName() + " key: " + key.toString() + " --> \nvalue:\n" + value.toString());
    }

    @Override
    public Configuration getConfiguration() {
        return this.config;
    }

    @Override
    public void setConfiguration(Configuration config) {
        this.config = config;
    }
}
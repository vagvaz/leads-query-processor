package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.StringConstants;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.Converter;
import org.infinispan.notifications.KeyFilter;
import org.infinispan.notifications.KeyValueFilter;
import org.infinispan.remoting.transport.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by vagvaz on 5/18/14.
 */

/**
 * Implementation of InfinispanManager interface
 * This class uses local operations to its embedded cache manager
 * in order to perform the operations for caches and listeners.
 */
public class LocalInfinispanManager implements InfinispanManager {
    String configurationFile;
    EmbeddedCacheManager manager;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public LocalInfinispanManager() {

    }

    public LocalInfinispanManager(EmbeddedCacheManager cacheManager) {
        this.manager = cacheManager;
    }

    @Override
    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void startManager(String configurationFile) {

        try {
            if (configurationFile != null && !configurationFile.equals("")) {
                manager = new DefaultCacheManager(configurationFile);
            } else {
                manager = new DefaultCacheManager(StringConstants.ISPN_DEFAULT_FILE);
                manager.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public EmbeddedCacheManager getCacheManager() {
        return manager;
    }

    @Override
    public void stopManager() {
        manager.stop();
    }

    @Override
    public ConcurrentMap getPersisentCache(String name) {
        return manager.getCache(name, true);
    }

    @Override
    public ConcurrentMap getPersisentCache(String name, Configuration configuration) {
        manager.defineConfiguration(name, configuration);
        return this.getPersisentCache(name);
    }


    @Override
    public void removePersistentCache(String name) {
        try {
            if (manager.cacheExists(name)) {
                manager.removeCache(name);
            }
        } catch (Exception e) {
            System.out.println("MSG: " + e.getMessage());
        }
    }

    @Override
    public void addListener(Object listener, Cache cache) {
        cache.addListener(listener);
    }


    @Override
    public void addListener(Object listener, String name) {
        Cache c = (Cache) this.getPersisentCache(name);
        this.addListener(listener, c);
    }

    @Override
    public void addListener(Object listener, String name, KeyFilter filter) {
        Cache c = (Cache) this.getPersisentCache(name);
        this.addListener(listener, c, filter);
    }

    @Override
    public void addListener(Object listener, String name, KeyValueFilter filter, Converter converter) {
        Cache c = (Cache) this.getPersisentCache(name);
        c.addListener(listener, filter, converter);
    }

    @Override
    public void addListener(Object listener, Cache cache, KeyFilter filter) {
        cache.addListener(listener, filter);
    }

    @Override
    public void addListener(Object listener, Cache cache, KeyValueFilter filter, Converter converter) {
        cache.addListener(listener, filter, converter);
    }

    @Override
    public void removeListener(Object listener, Cache cache) {
        cache.removeListener(listener);
    }

    @Override
    public void removeListener(Object listener, String cacheNane) {
        Cache cache = (Cache) getPersisentCache(cacheNane);
        removeListener(listener, cache);
    }


    @Override
    public List<Address> getMembers() {
        return this.manager.getMembers();
    }

    @Override
    public Address getMemberName() {
        return this.manager.getAddress();
    }

    @Override
    public boolean isStarted() {
        return manager.getStatus().equals(ComponentStatus.RUNNING);
    }
}

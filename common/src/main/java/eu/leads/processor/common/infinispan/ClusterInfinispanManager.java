package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.utils.PrintUtilities;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by vagvaz on 5/23/14.
 */

/**
 * Implementation of InfinispanManager interface
 * This class uses the Distributed Execution of infinispan
 * in order to perform the operations for caches and listeners.
 */
public class ClusterInfinispanManager implements InfinispanManager {


    private Logger log = LoggerFactory.getLogger(this.getClass());
    private EmbeddedCacheManager manager;
    private String configurationFile;

    public ClusterInfinispanManager() {
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
                manager = new DefaultCacheManager(StringConstants.ISPN_CLUSTER_FILE);
                manager.start();
                getPersisentCache("clustered");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PrintUtilities.printList(manager.getMembers());
                System.out.println("---- La La -----------");
                PrintUtilities.printList(manager.getCache().getAdvancedCache().getRpcManager().getMembers());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EmbeddedCacheManager getCacheManager() {
        return this.manager;
    }

    @Override
    public void stopManager() {

        this.manager.stop();
    }

    @Override
    public ConcurrentMap getPersisentCache(String name) {
        if (manager.cacheExists(name))
            manager.getCache(name);
        else {
            createCache(name, manager.getDefaultCacheConfiguration());
        }
        return manager.getCache(name);
    }

    @Override
    public ConcurrentMap getPersisentCache(String name, Configuration configuration) {
        if (manager.cacheExists(name))
            manager.getCache(name);
        else {
            createCache(name, configuration);
        }
        return manager.getCache(name);
    }

    private void createCache(String cacheName, Configuration cacheConfiguration) {
        if (!cacheConfiguration.clustering().cacheMode().isClustered()) {
            log.error("Configuration given for " + cacheName + " is not clustered so using default cluster configuration");
//            cacheConfiguration = new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_ASYNC).async().l1().lifespan(100000L).hash().numOwners(3).build();
        }
        DistributedExecutorService des = new DefaultExecutorService(manager.getCache());
        List<Future<Void>> list = des.submitEverywhere(new StartCacheCallable(cacheName));

        System.out.println("list " + list.size());
        for (Future<Void> future : list) {
            try {
                future.get(); // wait for task to complete
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
    }

    @Override
    public void removePersistentCache(String name) {
        removeCache(name);
    }

    private void removeCache(String name) {
        DistributedExecutorService des = new DefaultExecutorService(manager.getCache());
        List<Future<Void>> list = des.submitEverywhere(new StopCacheCallable(name));
        for (Future<Void> future : list) {
            try {
                future.get(); // wait for task to complete
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
    }

    @Override
    public void addListener(Object listener, Cache cache) {

        DistributedExecutorService des = new DefaultExecutorService(cache);
        List<Future<Void>> list = new LinkedList<Future<Void>>();
        for (Address a : getMembers()) {
//            des.submitEverywhere(new AddListenerCallable(cache.getName(),listener));
            try {
                list.add(des.submit(a, new AddListenerCallable(cache.getName(), listener)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


        for (Future<Void> future : list) {
            try {
                future.get(); // wait for task to complete
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
    }

    @Override
    public void addListener(Object listener, String name) {
        Cache c = (Cache) this.getPersisentCache(name);
        addListener(listener, c);
    }

    @Override
    public void addListener(Object listener, String name, KeyFilter filter) {
        Cache c = (Cache) this.getPersisentCache(name);
        c.addListener(listener, filter);
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
//        cache.addListener(listener,filter,converter);
    }

    @Override
    public void removeListener(Object listener, Cache cache) {
        DistributedExecutorService des = new DefaultExecutorService(cache);
        List<Future<Void>> list = new LinkedList<Future<Void>>();
        for (Address a : getMembers()) {
//            des.submitEverywhere(new AddListenerCallable(cache.getName(),listener));
            try {
                list.add(des.submit(a, new RemoveListenerCallable(cache.getName(), listener)));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }


        for (Future<Void> future : list) {
            try {
                future.get(); // wait for task to complete
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
    }

    @Override
    public void removeListener(Object listener, String cacheName) {
        Cache cache = (Cache) getPersisentCache(cacheName);
        removeListener(listener, cache);
    }

    @Override
    public List<Address> getMembers() {
        return manager.getCache("clustered").getAdvancedCache().getRpcManager().getMembers();
    }

    @Override
    public Address getMemberName() {
        return manager.getAddress();
    }

    @Override
    public boolean isStarted() {
        return manager.getStatus().equals(ComponentStatus.RUNNING);
    }
}

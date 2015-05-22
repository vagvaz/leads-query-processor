package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * Created by vagvaz on 3/7/15.
 */
public class EnsembleCacheUtils {
    static Logger profilerLog = LoggerFactory.getLogger("###PROF###" + EnsembleCacheUtils.class);
    static ProfileEvent profExecute =
        new ProfileEvent("Execute " + EnsembleCacheUtils.class, profilerLog);
    static Logger log = LoggerFactory.getLogger(EnsembleCacheUtils.class);
    static boolean useAsync;
    static Queue<NotifyingFuture<Void>> concurrentQuue;
    static Map<String, BasicCache> currentCaches;
    static Map<String, Map<Object, Object>> mapsToPut;
    static Set<Thread> threads;
    static volatile Object mutex = new Object();
    static Boolean initialized = false;
    static int batchSize = 20;
    static long counter = 0;
    static long threadCounter = 0;
    static long threadBatch = 4;
    private static ClearCompletedRunnable ccr;

    public static void initialize() {
        synchronized (mutex) {
            if (initialized) {
                return;
            }
            useAsync = LQPConfiguration.getInstance().getConfiguration()
                .getBoolean("node.infinispan.putasync", true);
            log.info("Using asynchronous put " + useAsync);
            concurrentQuue = new ConcurrentLinkedQueue<>();
            threads = new HashSet<>();
            //            ccr = new ClearCompletedRunnable(concurrentQuue,mutex,threads);
            initialized = true;
            batchSize = LQPConfiguration.getInstance().getConfiguration()
                .getInt("node.ensemble.batchsize", 10);
            currentCaches = new ConcurrentHashMap<>();
            mapsToPut = new ConcurrentHashMap<>();
        }
    }

    public static void waitForAllPuts() {
        profExecute.start("waitForAllPuts");
        while (!concurrentQuue.isEmpty()) {
            Iterator<NotifyingFuture<Void>> iterator = concurrentQuue.iterator();
            while (iterator.hasNext()) {
                NotifyingFuture current = iterator.next();
                try {
                    //                    if (current.isDone()) {
                    //                        iterator.remove();
                    //                    }
                    //                    else{
                    current.get();
                    iterator.remove();
                } catch (Exception e) {
                    log.error(
                        "EnsembleCacheUtils waitForAllPuts Exception " + e.getClass().toString());
                    log.error(e.getStackTrace().toString());
                    e.printStackTrace();
                }
            }
        }
        Iterator<Thread> threadIterator = threads.iterator();
        while (threadIterator.hasNext()) {
            Thread t = threadIterator.next();
            try {
                t.join();
            } catch (InterruptedException e) {
                log.error("EnsembleCacheUtils waitForAllPuts Wait clean threads " + e.getClass()
                    .toString());
                log.error(e.getStackTrace().toString());
                e.printStackTrace();
            }
        }
        profExecute.end();
    }

    public static void putToCache(BasicCache cache, Object key, Object value) {
        if (useAsync) {
            putToCacheAsync(cache, key, value);
            if (counter % batchSize == 0) {
                clearCompleted();
            }
            return;
        }
        putToCacheSync(cache, key, value);
    }

    private static void clearCompleted() {
        Map<String,BasicCache> caches;
        Map<String,Map<Object,Object>> objects;
        synchronized (mutex) {
            threadCounter = threads.size();
            if(threadCounter > threadBatch){
                for(Thread t : threads){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            caches = currentCaches;
            objects = mapsToPut;

            currentCaches = new ConcurrentHashMap<>();
            mapsToPut = new ConcurrentHashMap<>();
            BatchPutAllAsyncThread batchPutAllAsyncThread = new BatchPutAllAsyncThread(caches,objects);
            batchPutAllAsyncThread.start();
            threads.add(batchPutAllAsyncThread);
        }


        //        if(ccr == null)
        //        {
        //            ccr = new ClearCompletedRunnable(concurrentQuue,mutex,threads);
        //        }
        //        threads.add(ccr);
        //        ccr.start();
        //          ccr.run();
    }

    private static void putToCacheSync(BasicCache cache, Object key, Object value) {
        profExecute.start("putToCache Sync");
        boolean isok = false;
        while (!isok) {
            try {
                if (cache != null) {
                    if (key == null || value == null) {
                        log.error(
                            "SERIOUS PROBLEM with key/value null key: " + (key == null) + " value "
                                + (value == null));
                        if (key != null) {
                            log.error("key " + key.toString());
                        }
                        if (value != null) {
                            log.error("value: " + value);
                        }
                        isok = true;
                        continue;
                    }
                    cache.put(key, value);

                    //              log.error("Successful " + key);
                    isok = true;
                } else {
                    log.error("CACHE IS NULL IN PUT TO CACHE for " + key.toString() + " " + value
                        .toString());
                    isok = true;
                }
            } catch (Exception e) {
                isok = false;

                log.error("PUT TO CACHE " + e.getMessage() + " " + key);
                log.error("key " + (key == null) + " value " + (value == null) + " cache " + (cache
                    == null) + " log " + (log == null));

                System.err.println("PUT TO CACHE " + e.getMessage());
                e.printStackTrace();
                if (e.getMessage().startsWith("Cannot perform operations on ")) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        profExecute.end();
    }

    private static void putToCacheAsync(BasicCache cache, Object key, Object value) {
        counter = (counter + 1) % Long.MAX_VALUE;
        profExecute.start("putToCache Async");
        boolean isok = false;
        while (!isok) {
            try {
                if (cache != null) {
                    if (key == null || value == null) {
                        log.error(
                            "SERIOUS PROBLEM with key/value null key: " + (key == null) + " value "
                                + (value == null));
                        if (key != null) {
                            log.error("key " + key.toString());
                        }
                        if (value != null) {
                            log.error("value: " + value);
                        }
                        isok = true;
                        continue;
                    }
                    //                    NotifyingFuture fut = cache.putAsync(key, value);
                    //                    concurrentQuue.add(fut);
                    BasicCache currentCache = currentCaches.get(cache.getName());
                    if (currentCache == null) {
                        synchronized (mutex) {
                            currentCaches.put(cache.getName(), cache);

                            Map<Object, Object> newMap = new ConcurrentHashMap<>();
                            newMap.put(key, value);
                            mapsToPut.put(cache.getName(), newMap);
                        }
                    } else {
                        Map<Object, Object> cacheMap = mapsToPut.get(cache.getName());
                        if(cacheMap == null){
                            synchronized (mutex){
                                cacheMap = new ConcurrentHashMap<>();
                                mapsToPut.put(cache.getName(),cacheMap);
                            }
                        }
                        cacheMap.put(key, value);
                    }

                    //              log.error("Successful " + key);
                    isok = true;
                } else {
                    log.error("CACHE IS NULL IN PUT TO CACHE for " + key.toString() + " " + value
                        .toString());
                    isok = true;
                }
            } catch (Exception e) {
                isok = false;

                log.error("PUT TO CACHE " + e.getMessage() + " " + key);
                log.error("key " + (key == null) + " value " + (value == null) + " cache " + (cache
                    == null) + " log " + (log == null));

                System.err.println("PUT TO CACHE " + e.getMessage());
                e.printStackTrace();
                if (e.getMessage().startsWith("Cannot perform operations on ")) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        profExecute.end();
    }

    public static <KOut> void putIfAbsentToCache(BasicCache cache, KOut key, KOut value) {
        putToCache(cache, key, value);
        //    boolean isok = false;
        //    while(!isok) {
        //      try {
        //        cache.put(key, value);
        //        isok =true;
        //      } catch (Exception e) {
        //        isok = false;
        //        System.err.println("PUT TO CACHE " + e.getMessage());
        //      }
        //    }
    }
}

package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.commons.api.BasicCache;
import org.jgroups.util.ConcurrentLinkedBlockingQueue2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by vagvaz on 3/7/15.
 */
public class EnsembleCacheUtils {
    private static Logger profilerLog = LoggerFactory.getLogger("###PROF###" + EnsembleCacheUtils.class);
    private static ProfileEvent profExecute =
        new ProfileEvent("Execute " + EnsembleCacheUtils.class, profilerLog);
    private static Logger log = LoggerFactory.getLogger(EnsembleCacheUtils.class);
    private static boolean useAsync;
    //    static Queue<NotifyingFuture<Void>> concurrentQuue;
    private static Map<String, BasicCache> currentCaches;
    private static Map<String, Map<Object, Object>> mapsToPut;
    private static Queue<Thread> threads;
    private static volatile Object mutex = new Object();
    private static Boolean initialized = false;
    private static int batchSize = 20;
    private static long counter = 0;
    private static long threadCounter = 0;
    private static long threadBatch = 3;
    private static ProfileEvent putEvent;
    private static ThreadPoolExecutor executor;
    private static ConcurrentLinkedDeque<SyncPutRunnable> runnables;
//    private static ClearCompletedRunnable ccr;
    private static volatile Object runnableMutex = new Object();
    public static void initialize() {
        synchronized (mutex) {
            if (initialized) {
                return;
            }
            if(currentCaches != null || mapsToPut != null)
            {
                System.err.println("SERIOUS ERRROR " + (currentCaches == null) +" "+ (mapsToPut == null) );
                System.exit(-1);
            }
            useAsync = LQPConfiguration.getInstance().getConfiguration()
                .getBoolean("node.infinispan.putasync", true);
            log.info("Using asynchronous put " + useAsync);
            //            concurrentQuue = new ConcurrentLinkedQueue<>();
            threads = new ConcurrentLinkedQueue<>();
            //            ccr = new ClearCompletedRunnable(concurrentQuue,mutex,threads);

            batchSize = LQPConfiguration.getInstance().getConfiguration()
                .getInt("node.ensemble.batchsize", 10);
            threadBatch = LQPConfiguration.getInstance().getConfiguration().getInt(
                "node.ensemble.threads", 3);

            System.out.println("threads " + threadBatch + " batchSize " + batchSize + " async = " + useAsync);
            currentCaches = new ConcurrentHashMap<>();
            mapsToPut = new ConcurrentHashMap<>();
            initialized = true;
            executor = new ThreadPoolExecutor((int)threadBatch,(int)(1.1*threadBatch),5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
            runnables = new ConcurrentLinkedDeque<>();
            for (int i = 0; i < 50 * (threadBatch); i++) {
                runnables.add(new SyncPutRunnable());
            }
//            executor.prestartAllCoreThreads();
        }
    }

    public  static SyncPutRunnable getRunnable(){
        SyncPutRunnable result = null;
        synchronized (runnableMutex){
            result = runnables.poll();
            while(result == null){
                try {
                    runnableMutex.wait(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result = runnables.poll();
            }
        }

        return result;
    }

    public static void addRunnable(SyncPutRunnable runnable){
        synchronized (runnableMutex){
            runnables.add(runnable);
            runnableMutex.notify();
        }
    }
    public static void waitForAllPuts() {
        //        profExecute.start("waitForAllPuts");
        synchronized (runnableMutex){
            runnableMutex.notifyAll();
        }
        while(executor.getActiveCount() > 0)
        try {
//            executor.awaitTermination(100,TimeUnit.MILLISECONDS);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        if(!executor.isShutdown())
//            executor.shutdown();
//            Thread t = threads.poll();
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                log.error("EnsembleCacheUtils waitForAllPuts Wait clean threads " + e.getClass()
//                    .toString());
//                log.error(e.getStackTrace().toString());
//                e.printStackTrace();
//            }
//        }
        //        profExecute.end();
    }

    public static void putToCache(BasicCache cache, Object key, Object value) {
        putEvent = new ProfileEvent("profPutToCache",profilerLog);
        if (useAsync) {
            putToCacheAsync(cache, key, value);
//            if (counter % batchSize == 0) {
//                clearCompleted();
//            }
            return;
        }
        putToCacheSync(cache, key, value);
        putEvent.end();
    }

    private static void clearCompleted() {
        Map<String,BasicCache> caches;
        Map<String,Map<Object,Object>> objects;
        List<Thread> completedThreads = new LinkedList<>();
        synchronized (mutex) {
            threadCounter = threads.size();
            //      System.err.println("Active threads: " + threadCounter);
            if(threadCounter > threadBatch){
                for(Thread t : threads){
                    if(!t.isAlive()){
                        completedThreads.add(t);
                    }

                }
                //        System.err.println("Completed threads: " + completedThreads.size());
                while(threads.size() - completedThreads.size() > threadBatch) {
                    for (Thread t : threads) {
                        try {
                            t.join(100);
                            if (!t.isAlive()) {
                                completedThreads.add(t);
                                if(threads.size() - completedThreads.size() < threadBatch) {
                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            log.error("EnsembleCacheUtilsClearCompletedException " + e.getMessage());
                            PrintUtilities.logStackTrace(log, e.getStackTrace());
                        }
                    }
                }
            }
            for(Thread t : completedThreads){
                threads.remove(t);
            }
            //      System.err.println("After cleanup Active threads: " + threads.size());
            assert (threads.size() < threadBatch);
            caches = currentCaches;
            objects = mapsToPut;

            currentCaches = new ConcurrentHashMap<>();
            mapsToPut = new ConcurrentHashMap<>();
            BatchPutAllAsyncThread batchPutAllAsyncThread = new BatchPutAllAsyncThread(caches,objects);
//            threads.add(batchPutAllAsyncThread);
//            batchPutAllAsyncThread.start();
        }
    }

    private static void putToCacheSync(BasicCache cache, Object key, Object value) {
        //        profExecute.start("putToCache Sync");
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
        //        profExecute.end();
    }

    private static void putToCacheAsync(final BasicCache cache, final Object key, final Object value) {
//        counter = (counter + 1) % Long.MAX_VALUE;
        //        profExecute.start("putToCache Async");
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
//                    synchronized (mutex) {
//                        BasicCache currentCache = currentCaches.get(cache.getName());
//                        if (currentCache == null) {

//                            currentCaches.put(cache.getName(), cache);

//                            Map<Object, Object> newMap = new ConcurrentHashMap<>();
//                            newMap.put(key, value);
//                            mapsToPut.put(cache.getName(), newMap);
//                        } else {
//                            Map<Object, Object> cacheMap = mapsToPut.get(cache.getName());
//                            if (cacheMap == null) {
////                                synchronized (mutex) {
//                                    cacheMap = new ConcurrentHashMap<>();
//
//                                    mapsToPut.put(cache.getName(), cacheMap);
////                                }
//                            }
//                            if(cacheMap.containsKey(key))
//                            {
//                                System.err.println("ERROR: " + currentCache.getName() + " already contains key " + key.toString() + " with value\n" + cacheMap.get(key).toString() );
//                                System.exit(-1);
//                            }
//                            cacheMap.put(key, value);
//                        }
//                    }
                    //              log.error("Successful " + key);

                    SyncPutRunnable putRunnable = EnsembleCacheUtils.getRunnable();
                    putRunnable.setParameters(cache,key,value);
                    executor.submit(putRunnable);
                    isok = true;
                } else {
                    log.error("CACHE IS NULL IN PUT TO CACHE for " + key.toString() + " " + value
                        .toString());
                    isok = true;
                }
            } catch (Exception e) {
                isok = false;
                if(e instanceof RejectedExecutionException){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
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
        //        profExecute.end();
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

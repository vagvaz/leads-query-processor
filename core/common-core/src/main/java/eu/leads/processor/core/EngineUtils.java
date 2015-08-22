package eu.leads.processor.core;

import eu.leads.processor.common.infinispan.SyncPutRunnable;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.infinispan.ExecuteRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by vagvaz on 8/22/15.
 */
public class EngineUtils {
    private static Logger log = LoggerFactory.getLogger(EngineUtils.class);
    private static int threadBatch;
    private static ThreadPoolExecutor executor;
    private static ConcurrentLinkedDeque<ExecuteRunnable> runnables;
    private static volatile Object mutex;
    private static boolean initialized = false;

    public static void initialize() {
        synchronized (mutex) {
            if (initialized) {
                return;
            }

            threadBatch = LQPConfiguration.getInstance().getConfiguration().getInt(
                "node.engine.threads", 1);

            System.out.println("Executor threads " + threadBatch + "  " );
            initialized = true;
            executor = new ThreadPoolExecutor((int)threadBatch,(int)(2*threadBatch),2000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
            runnables = new ConcurrentLinkedDeque<>();
            for (int i = 0; i <= 2 * (threadBatch); i++) {
                runnables.add(new ExecuteRunnable());
            }
        }
    }

    public  static ExecuteRunnable getRunnable(){
        ExecuteRunnable result = null;
        //        synchronized (runnableMutex){
        result = runnables.poll();
        while(result == null){
                            try {
            //                    Thread.sleep(1);
            Thread.sleep(0,10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
            result = runnables.poll();
            //            }
        }

        return result;
    }

    public static void addRunnable(ExecuteRunnable runnable){
        //        synchronized (runnableMutex){
        runnables.add(runnable);
        //            runnableMutex.notify();
        //        }
    }
    public static void waitForAllExecute() {

        while(executor.getActiveCount() > 0)
            try {
                //            executor.awaitTermination(100,TimeUnit.MILLISECONDS);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static void submit(ExecuteRunnable runable) {
        executor.submit(runable);
    }
}

package eu.leads.distsum;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.SingleCacheManagerTest;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.Test;

import java.util.*;

/**
 *
 * @author  vagvaz
 * @author otrack
 *
 * Created by vagvaz on 7/5/14.
 *
 * Scenario:
 *
 * There is a group of nodes that monitor some streams of updates. We want to compute the total sum of these updates.
 * One node from the group acts as a coordinator and the rest of the nodes as workers.
 * The coordinator maintains the global sum, on the other hand, the worker nodes listen to the streams
 * of updates and maintain a local sum. Furthermore, the worker nodes have some all_deltas, in our case
 * these all_deltas are two numbers an upper and a lower  bound. In case of an update violates the all_deltas,
 * then the worker informs the coordinator, which in turn asks all workers to
 * send their local values in order to recompute the global sum and the new all_deltas for each node.
 * After these recomputations, the coordinator sends the new all_deltas back to the workers.
 *
 * Scenario:
 * We have one coordinator and 3 worker nodes. We have 4 rounds. In each round we update each worker once. Each worker's update will be chosen
 * uniformly from the  following array   {1,1,1,1,2,2,-1,-1,-2}. At the end of each round we get the global sum perceived from the coordinator
 * Mind that the global sum might not be the actual sum, but it must always be inside the following values 0.9*realSum<= globalSum <= 1.1realSum.
 *
 * Using KVS/Infinispan as a communication channel:
 *
 * I tried to keep the code simple in order to demonstrate the communication needs and not to perplex things, by doing it
 * using infinispan listeners. Using infinispan we would have one cache that would generate the updates (updateCache).
 * On that cache we would have installed the worker listeners. The worker listeners whenever they would like to communication
 * with the coordinator, they would do a put operation on another cache (workerCache). The coordinator could be a clustered listener
 * installed on the workerCache, as a result, it listens to all the updates. What I cannot think is how through listeners the communication
 * from the coordinator to the workers can be achieved. I would not like to do updates to the updateCache. To sup up.
 * We have a clustered listener, the coordinator, installed on workerCache,
 * we have the worker listeners, worker nodes, installed locally to all the nodes containing keys of the updateCache
 * whenever worker nodes need to communicate with the coordinator do a put operation to the workerCache
 * whener the Coordinator wants to communicate with the workers ??
 *
 * Using the distributed executor could be a solution.
 *
 *
 */
@Test
public class UnitTest extends SingleCacheManagerTest{

    public void run() {

        Random rand = new Random();

        //The communication channel between coordinator and the workers
        ComChannel channel = new ComChannel(cacheManager.<String, Message>getCache());
        Coordinator coord = new Coordinator(channel);


        ArrayList<Worker> workers = new ArrayList<Worker>();
        //Create initial values and all_deltas for the workers
        /*Map<String,Integer> workerValues = new HashMap<String, Integer>(numOfWorkers);
        Map<String,Constrain> workerConstrains = new HashMap<String, Constrain>(numOfWorkers);*/

        int numOfWorkers = 100;
        for ( int worker = 0; worker < numOfWorkers; worker++ ) {
            //Create new worker with initial values
            Worker w = new Worker(Integer.toString(worker), channel);
            workers.add(w);
            //register worker to the channel
            channel.register(w.getId(),w);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("------------");

        int[] updates = {1,1,1,1,2,2,-1,-1,-2};
        int numberOfRounds = 100;

        for ( int round = 0; round < numberOfRounds; round++ ) {
            System.out.println("********* ROUND " + round +" ********");

            for ( int worker = 0; worker < numOfWorkers; worker++ ) {

                //int objToUpd = (int) rand.nextGaussian() + 1000;
                int objToUpd = rand.nextInt(100);
                int update = updates[rand.nextInt(updates.length)];

                workers.get(worker).update(objToUpd, update);

            }

        }


    }

    @Override
    protected EmbeddedCacheManager createCacheManager() throws Exception {
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        ConfigurationBuilder config = TestCacheManagerFactory.getDefaultCacheConfiguration(false);
        return TestCacheManagerFactory.createCacheManager(global, config);
    }
}

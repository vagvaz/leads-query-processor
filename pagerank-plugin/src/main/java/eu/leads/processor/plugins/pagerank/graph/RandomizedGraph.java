package eu.leads.processor.plugins.pagerank.graph;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import comm.ComChannel;
import comm.Coordinator;
import comm.Message;
import comm.Worker;
import eu.leads.processor.common.infinispan.InfinispanManager;
<<<<<<< HEAD
import eu.leads.processor.plugins.pagerank.utils.Const;
=======
>>>>>>> ioakeim
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class RandomizedGraph {

<<<<<<< HEAD
   protected static RandomEngine generator;
   protected static Uniform unif;
   protected Cache graphCache;
   protected Cache vc_per_node;
   protected int R;
   protected Logger log = LoggerFactory.getLogger(RandomizedGraph.class);
=======
    protected Cache graphCache;
    protected Cache approx_sum_cache;
>>>>>>> ioakeim

   protected String nodeName;
   protected int accurateLocalSum;

   public RandomizedGraph(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {
      initCacheAndAttrs(configuration, infinispanManager);
      generator = new cern.jet.random.engine.MersenneTwister64(seed);
      unif = new Uniform(generator);
      this.R = R;

      nodeName = infinispanManager.getCacheManager().getAddress().toString();
      accurateLocalSum = 0;
   }

<<<<<<< HEAD
   private void initCacheAndAttrs(Configuration configuration, InfinispanManager infinispanManager) {
      String targetCacheName = configuration.getString("cache");
      if (targetCacheName != null || !targetCacheName.equals("")) {
         graphCache = (Cache) infinispanManager.getPersisentCache(targetCacheName);
      } else {
         log.error("TargetCache is not defined using default for not breaking");
         graphCache = (Cache) infinispanManager.getPersisentCache("default");
      }

      vc_per_node = (Cache) infinispanManager.getPersisentCache(configuration.getString("vc_cache"));
      vc_per_node.put(Const.GLOBAL_SUM, 0);

   }

   public Cache getVc_per_node() {
      return vc_per_node;
   }

   public void setVc_per_node(Cache vc_per_node) {
      this.vc_per_node = vc_per_node;
   }

   public Cache getGraphCache() {
      return graphCache;
   }

   public void setGraphCache(Cache graphCache) {
      this.graphCache = graphCache;
   }

   public int getR() {
      return R;
   }

   public void setR(int r) {
      R = r;
   }
=======
    protected String nodeName;
    //protected int accurateLocalSum;

    protected ComChannel channel;
    protected Coordinator coord;

    public RandomizedGraph(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {


        initCacheAndAttrs(configuration, infinispanManager);
        generator = new cern.jet.random.engine.MersenneTwister64(seed);
        unif = new Uniform(generator);
		this.R = R;

        nodeName = infinispanManager.getCacheManager().getAddress().toString();
        //accurateLocalSum = 0;

        //Setup the communication enabled via Infinispan KVS
        //The communication channel between coordinator and the workers
        channel = new ComChannel(infinispanManager.getCacheManager().<String, Message>getCache()/*(Cache) infinispanManager.getPersisentCache("PageRankComCache")*/);
        coord = new Coordinator(channel);
	}

    private void initCacheAndAttrs(Configuration configuration, InfinispanManager infinispanManager){

        String targetCacheName = configuration.getString("cache");
        if(targetCacheName != null || !targetCacheName.equals("")) {
            graphCache = (Cache) infinispanManager.getPersisentCache(targetCacheName);
        }else{
            log.error("TargetCache is not defined using default for not breaking");
            graphCache = (Cache) infinispanManager.getPersisentCache("default");
        }

        approx_sum_cache = (Cache) infinispanManager.getPersisentCache(configuration.getString("vc_cache"));
        //approx_sum_cache.put(Const.GLOBAL_SUM, 0);

    }


    public Coordinator getCoord() {
        return coord;
    }

    public void setCoord(Coordinator coord) {
        this.coord = coord;
    }


    public Cache getApprox_sum_cache(){
        return approx_sum_cache;
    }

    /*public void setApprox_sum_cache(Cache approx_sum_cache){
        this.approx_sum_cache = approx_sum_cache;
    }*/

    public Cache getGraphCache(){
        return graphCache;
    }
>>>>>>> ioakeim


   public void sendVisitDriftIfNeeded(int drift) {

      accurateLocalSum += drift;
      //VCperCloud[tmpNode.getMyCloud()] += drift;

      if (vc_per_node.containsKey(nodeName)) {
         int old_estimate = (Integer) vc_per_node.get(nodeName);

         if (Math.abs(accurateLocalSum - old_estimate) >
                     (Const.VIS_COUNT_DRIFT * accurateLocalSum)) {

<<<<<<< HEAD
            vc_per_node.put(nodeName, accurateLocalSum);
            vc_per_node.put(Const.GLOBAL_SUM, (Integer) vc_per_node.get(Const.GLOBAL_SUM) + (accurateLocalSum - old_estimate));
         }
      } else {
         vc_per_node.put(nodeName, accurateLocalSum);
         vc_per_node.put(Const.GLOBAL_SUM, (Integer) vc_per_node.get(Const.GLOBAL_SUM) + accurateLocalSum);
      }
=======
        //accurateLocalSum += drift;

        //VCperCloud[tmpNode.getMyCloud()] += drift;

        if ( approx_sum_cache.containsKey(nodeName) ){

            ((Worker) approx_sum_cache.get(nodeName)).update(drift);
            /*int old_estimate = ((Worker) approx_sum_cache.get(nodeName)).getPrevReported();

            if ( Math.abs( accurateLocalSum - old_estimate ) >
                    ( Const.VIS_COUNT_DRIFT * accurateLocalSum ) ){

                approx_sum_cache.put(nodeName, accurateLocalSum);
                approx_sum_cache.put(Const.GLOBAL_SUM, (Integer) approx_sum_cache.get(Const.GLOBAL_SUM) +  (accurateLocalSum - old_estimate)  );
            }*/
        }
        else{

            Worker w = new Worker(nodeName, 0, channel);
            w.update(drift);
            approx_sum_cache.put(nodeName, w);

            // approx_sum_cache.put(Const.GLOBAL_SUM, (Integer) approx_sum_cache.get(Const.GLOBAL_SUM) +  accurateLocalSum  );
        }
>>>>>>> ioakeim

        /*if ( Math.abs( VCperCloud[tmpNode.getMyCloud()] - estVCperCloud[tmpNode.getMyCloud()] ) >
                ( Main.VIS_COUNT_DRIFT * VCperCloud[tmpNode.getMyCloud()] ) ){

            estVCperCloud[tmpNode.getMyCloud()] = VCperCloud[tmpNode.getMyCloud()];

            cm.increaseLW( -1, -2, 2 );
            cm.sendMsgs( -1, -2, 1 );
        }*/

   }

   //public abstract void propagateWalks();

   public int countValuesOfMap(TObjectIntHashMap map) {
      TObjectIntIterator it = map.iterator();
      int y = 0;
      while (it.hasNext()) {
         it.advance();
         y += it.value();
      }
      return y;
   }

   public void realTimeFilling(String tempPath) throws IOException {
      //assert(this instanceof FIP || this instanceof DSPM || this instanceof Bahmani);

      BufferedReader br = new BufferedReader(new FileReader(tempPath));
      String sCurrentLine;
      String[] edge;

      while ((sCurrentLine = br.readLine()) != null) {
         edge = sCurrentLine.split("\t");

         //if (this instanceof DSPM)
         ((DSPM) this).processEdge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1]));
         //else if (this instanceof FIP)
         //((FIP) this).processEdge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1]));
         //	assert(1>2);
         //else
         //	((Bahmani) this).updateWalks(Integer.parseInt(edge[0]), Integer.parseInt(edge[1]));

      }
      br.close();
   }

   public Object returnRandomNeighbour(THashSet tempSet) {

      if (tempSet.size() == 1)
         return tempSet.iterator().next();

      int pos = unif.nextIntFromTo(0, tempSet.size() - 1), i = 0;
      Object tmp;
      TObjectHashIterator myIt = tempSet.iterator();

      while (myIt.hasNext()) {
         tmp = myIt.next();

         if (i == pos)
            return tmp;

         i++;
      }

      assert (1 > 2);
      return -1;
   }

}

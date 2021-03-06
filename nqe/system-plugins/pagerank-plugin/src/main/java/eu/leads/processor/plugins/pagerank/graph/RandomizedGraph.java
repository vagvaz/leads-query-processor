package eu.leads.processor.plugins.pagerank.graph;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import comm.ComChannel;
import comm.Worker;
import eu.leads.processor.common.infinispan.InfinispanManager;
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

    protected Cache graphCache;
    protected Cache approx_sum_cache;

    protected int R;

    protected static RandomEngine generator;
    protected static Uniform unif;

    protected Logger log = LoggerFactory.getLogger(RandomizedGraph.class);

    protected String nodeName;
    //protected int accurateLocalSum;

    protected ComChannel channel;
//    protected Coordinator coord;
    protected long localValue;
    private Worker w;

    public RandomizedGraph(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {


        initCacheAndAttrs(configuration, infinispanManager);
        generator = new cern.jet.random.engine.MersenneTwister64(seed);
        unif = new Uniform(generator);
		this.R = R;

        nodeName = infinispanManager.getCacheManager().getAddress().toString();
        //accurateLocalSum = 0;

        //Setup the communication enabled via Infinispan KVS
        //The communication channel between coordinator and the workers
//        channel = new ComChannel(infinispanManager.getCacheManager().<String, Message>getCache()/*(Cache) infinispanManager.getPersisentCache("PageRankComCache")*/);

        channel = new ComChannel(approx_sum_cache);
        w = new Worker(nodeName, 0, channel);
//        coord = new Coordinator(channel);

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


//    public Coordinator getCoord() {
//        return coord;
//    }
//
//    public void setCoord(Coordinator coord) {
//        this.coord = coord;
//    }


    public Cache getApprox_sum_cache(){
        return approx_sum_cache;
    }

    /*public void setApprox_sum_cache(Cache approx_sum_cache){
        this.approx_sum_cache = approx_sum_cache;
    }*/

    public Cache getGraphCache(){
        return graphCache;
    }

    public void setGraphCache(Cache graphCache){
        this.graphCache = graphCache;
    }

	public int getR() {
		return R;
	}

	public void setR(int r) {
		R = r;
	}


    public void sendVisitDriftIfNeeded(int drift){

        //accurateLocalSum += drift;
        if(w.update(drift))
            approx_sum_cache.put(nodeName,w.getLocalValue()+drift);
        //VCperCloud[tmpNode.getMyCloud()] += drift;

//        if ( approx_sum_cache.containsKey(nodeName) ){

//            ((Worker) approx_sum_cache.get(nodeName)).update(drift);
//            w.update(drift);
            /*int old_estimate = ((Worker) approx_sum_cache.get(nodeName)).getPrevReported();

            if ( Math.abs( accurateLocalSum - old_estimate ) >
                    ( Const.VIS_COUNT_DRIFT * accurateLocalSum ) ){

                approx_sum_cache.put(nodeName, accurateLocalSum);
                approx_sum_cache.put(Const.GLOBAL_SUM, (Integer) approx_sum_cache.get(Const.GLOBAL_SUM) +  (accurateLocalSum - old_estimate)  );
            }*/
//        }
//        else{


//            w.update(drift);
//            approx_sum_cache.put(nodeName, w);

            // approx_sum_cache.put(Const.GLOBAL_SUM, (Integer) approx_sum_cache.get(Const.GLOBAL_SUM) +  accurateLocalSum  );
//        }

        /*if ( Math.abs( VCperCloud[tmpNode.getMyCloud()] - estVCperCloud[tmpNode.getMyCloud()] ) >
                ( Main.VIS_COUNT_DRIFT * VCperCloud[tmpNode.getMyCloud()] ) ){

            estVCperCloud[tmpNode.getMyCloud()] = VCperCloud[tmpNode.getMyCloud()];

            cm.increaseLW( -1, -2, 2 );
            cm.sendMsgs( -1, -2, 1 );
        }*/

    }

	//public abstract void propagateWalks();

	public int countValuesOfMap(TObjectIntHashMap map){
		TObjectIntIterator it = map.iterator();
		int y = 0;
		while ( it.hasNext() ){
			it.advance();
			y += it.value();
		}
		return y;
	}
	
	public void realTimeFilling(String tempPath) throws IOException{
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

	public Object returnRandomNeighbour(THashSet tempSet){

		if (tempSet.size() == 1)
			return tempSet.iterator().next();

		int pos = unif.nextIntFromTo(0, tempSet.size()-1), i = 0;
        Object tmp;
		TObjectHashIterator myIt = tempSet.iterator();

		while (myIt.hasNext()){
			tmp = myIt.next();

			if (i == pos)
				return tmp;

			i++;
		}

		assert(1>2);
		return -1;
	}

}

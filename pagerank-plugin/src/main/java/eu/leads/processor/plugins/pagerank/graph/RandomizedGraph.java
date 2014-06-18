package eu.leads.processor.plugins.pagerank.graph;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import eu.leads.processor.common.infinispan.InfinispanManager;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class RandomizedGraph {

    protected Cache graphCache;
    protected int R;

    protected static RandomEngine generator;
    protected static Uniform unif;

    protected Logger log = LoggerFactory.getLogger(RandomizedGraph.class);

    public RandomizedGraph(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {
        initCacheAndAttrs(configuration, infinispanManager);
        generator = new cern.jet.random.engine.MersenneTwister64(seed);
        unif = new Uniform(generator);
		this.R = R;
	}

    private void initCacheAndAttrs(Configuration configuration, InfinispanManager infinispanManager){
        String targetCacheName = configuration.getString("cache");
        if(targetCacheName != null || !targetCacheName.equals("")) {
            graphCache = (Cache) infinispanManager.getPersisentCache(targetCacheName);
        }else{
            log.error("TargetCache is not defined using default for not breaking");
            graphCache = (Cache) infinispanManager.getPersisentCache("default");
        }
    }

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

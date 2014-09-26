package eu.leads.processor.plugins.pagerank.graph;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.plugins.pagerank.node.FIPNode;
import eu.leads.processor.plugins.pagerank.utils.Const;
import org.apache.commons.configuration.Configuration;

import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class FIP extends RandomizedGraph {

    public FIP(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {
        super(R, configuration, infinispanManager, seed);
    }

    protected void fip_random_walk(Object key, Object rwID, int curr_pos, boolean negative) {

        FIPNode tmpNode = (FIPNode) graphCache.get(key);

        if (negative) {

            assert (tmpNode.getFip_map().get(rwID).containsKey(curr_pos));

            Object next_pos = tmpNode.getFip_map().get(rwID).remove(curr_pos);
            if (tmpNode.getFip_map().get(rwID).isEmpty())
                tmpNode.getFip_map().remove(rwID);

            tmpNode.fipVisitsMinus1();
            assert (tmpNode.getFipVisits() >= 0);
            graphCache.put(key, tmpNode);

            sendVisitDriftIfNeeded(-1);

            if (!next_pos.equals(-1)) {
                assert (tmpNode.getNeighbours().contains(next_pos));

				/*if (COUNTME){
               cm.increaseHW(tmpNode.getMyCloud(), graph.get(next_pos).getMyCloud());
					cm.sendMsgs(tmpNode.getMyCloud(), graph.get(next_pos).getMyCloud(), 1);
				}*/

                fip_random_walk(next_pos, rwID, curr_pos + 1, negative);
            }
        } else {

            tmpNode.getFip_map().putIfAbsent(rwID, new TreeMap<Integer, Object>());
            if (tmpNode.getNeighbours().isEmpty()) {
                tmpNode.getFip_map().get(rwID).put(curr_pos, -1);
                tmpNode.fipVisitsPlus1();
                graphCache.put(key, tmpNode);
            } else {
                if (generator.raw() > Const.EPSILON) {

                    Object rand = returnRandomNeighbour(tmpNode.getNeighbours());
                    tmpNode.getFip_map().get(rwID).put(curr_pos, rand);
                    tmpNode.fipVisitsPlus1();
                    graphCache.put(key, tmpNode);

					/*if (COUNTME){
            cm.increaseHW(tmpNode.getMyCloud(), graph.get(rand).getMyCloud());
						cm.sendMsgs(tmpNode.getMyCloud(), graph.get(rand).getMyCloud(), 1);
					}*/
                    fip_random_walk(rand, rwID, curr_pos + 1, negative);
                } else {
                    tmpNode.getFip_map().get(rwID).put(curr_pos, -1);
                    tmpNode.fipVisitsPlus1();
                    graphCache.put(key, tmpNode);
                }
            }
            sendVisitDriftIfNeeded(1);

        }

    }

	/*protected void update_fip_walks(int U, TIntArrayList mylist){

		FIPNode nodeU = (FIPNode) graph.get(U);
		TIntIterator mylistiter = mylist.iterator();
		while (mylistiter.hasNext())
			nodeU.getNeighbours().add(mylistiter.next());

		TIntObjectIterator<TreeMap<Integer, Integer>> it = nodeU.getFip_map().iterator();

		if (nodeU.getNeighbours().size() == mylist.size()){

			while (it.hasNext()){
				it.advance();

				int lastKey = it.value().lastEntry().getKey(), tmpOut;
				assert ( it.value().lastEntry().getValue() == -1) ;

				if ( generator.raw() > Const.EPSILON ){
					tmpOut = mylist.get(unif.nextIntFromTo(0, mylist.size()-1));
					nodeU.getFip_map().get(it.key()).put(lastKey, tmpOut);

					*//*if (COUNTME){
						cm.increaseHW(nodeU.getMyCloud(), graph.get(tmpOut).getMyCloud());
						cm.sendMsgs(nodeU.getMyCloud(), graph.get(tmpOut).getMyCloud(), 1);
					}*//*
					fip_random_walk(tmpOut, it.key(), lastKey+1, false);
				}

			}
		}
		else{
			int outu = nodeU.getNeighbours().size(), tmpOut;
			double prob = mylist.size() / (double) outu;

			while (it.hasNext()){
				it.advance();

				for(Entry<Integer, Integer> entry : it.value().entrySet()){

					if (entry.getValue() == -1)
						continue;

					if ( generator.raw() <= prob ){

						*//*if (COUNTME){
							cm.increaseHW(nodeU.getMyCloud(), graph.get(entry.getValue()).getMyCloud());
							cm.sendMsgs(nodeU.getMyCloud(), graph.get(entry.getValue()).getMyCloud(), 1);
						}*//*
						fip_random_walk(entry.getValue(), it.key(), entry.getKey()+1, true); // negative walk

						tmpOut = mylist.get(unif.nextIntFromTo(0, mylist.size()-1));
						nodeU.getFip_map().get(it.key()).put(entry.getKey(), tmpOut);

						*//*if (COUNTME){
							cm.increaseHW(nodeU.getMyCloud(), graph.get(tmpOut).getMyCloud());
							cm.sendMsgs(nodeU.getMyCloud(), graph.get(tmpOut).getMyCloud(), 1);
						}*//*
						fip_random_walk(tmpOut, it.key(), entry.getKey()+1, false); // regular walk
						break; // break since the rest of RW is replaced
					}

				}

			}
		}
	}*/

    protected void update_fip_walks(Object U, Object V) {

        FIPNode nodeU = (FIPNode) graphCache.get(U);
        nodeU.getNeighbours().add(V);
        graphCache.put(U, nodeU);

        //TIterator it = nodeU.getFip_map().entrySet();

        if (nodeU.getNeighbours().size() == 1) {

            for (Entry<Object, TreeMap<Integer, Object>> e : nodeU.getFip_map().entrySet()) {

                int lastKey = e.getValue().lastEntry().getKey();
                assert (e.getValue().lastEntry().getValue().equals(-1));

                if (generator.raw() > Const.EPSILON) {
                    nodeU.getFip_map().get(e.getKey()).put(lastKey, V);
                    graphCache.put(U, nodeU);

					/*if (COUNTME){
						cm.increaseHW(nodeU.getMyCloud(), graph.get(V).getMyCloud());
						cm.sendMsgs(nodeU.getMyCloud(), graph.get(V).getMyCloud(), 1);
					}*/
                    fip_random_walk(V, e.getKey(), lastKey + 1, false);
                }

            }
        } else {

            int outu = nodeU.getNeighbours().size();
            double prob = 1 / (double) outu;

            for (Entry<Object, TreeMap<Integer, Object>> e : nodeU.getFip_map().entrySet()) {

                for (Entry<Integer, Object> entry : e.getValue().entrySet()) {

                    if (entry.getValue().equals(-1))
                        continue;

                    if (generator.raw() <= prob) {

						/*if (COUNTME){
							cm.increaseHW(nodeU.getMyCloud(), graph.get(entry.getValue()).getMyCloud());
							cm.sendMsgs(nodeU.getMyCloud(), graph.get(entry.getValue()).getMyCloud(), 1);
						}*/
                        fip_random_walk(entry.getValue(), e.getKey(), entry.getKey() + 1,
                                           true); // negative walk

                        nodeU.getFip_map().get(e.getKey()).put(entry.getKey(), V);
                        graphCache.put(U, nodeU);

						/*if (COUNTME){
							cm.increaseHW(nodeU.getMyCloud(), graph.get(V).getMyCloud());
							cm.sendMsgs(nodeU.getMyCloud(), graph.get(V).getMyCloud(), 1);
						}*/
                        fip_random_walk(V, e.getKey(), entry.getKey() + 1, false); // regular walk
                        break; // break since the rest of RW is replaced
                    }

                }

            }
        }
    }

}

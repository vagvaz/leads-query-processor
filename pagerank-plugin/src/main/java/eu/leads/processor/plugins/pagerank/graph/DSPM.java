package eu.leads.processor.plugins.pagerank.graph;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import eu.leads.processor.plugins.pagerank.node.Node;
import eu.leads.processor.plugins.pagerank.utils.Const;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.configuration.Configuration;

import java.util.*;

public class DSPM extends FIP  {

	//protected boolean init_phase;

	public DSPM(int R, Configuration configuration, InfinispanManager infinispanManager, int seed) {
		super(R, configuration, infinispanManager, seed);
        //init_phase = false;
	}
	
	/*public boolean isInit_phase() {
		return init_phase;
	}

	public void setInit_phase(boolean init_phase) {
		this.init_phase = init_phase;
	}*/
	
	public void processEdge(Object U, Object V){
		assert(!U.equals(V));

        if ( graphCache.get(V) == null ){
            graphCache.put(V, new DSPMNode(V));

			randomWalk(V, R, false);
			fip_random_walk(V, V, 1, false);
		}
		if ( graphCache.get(U) != null ){
			//assert ( !((Node)graphCache.get(U)).getNeighbours().contains(V) );

            if (!( ((DSPMNode) graphCache.get(U)).getNeighbours().contains(V) )){//link-addition inside func
                updateWalks( U, V );
                update_fip_walks( U, V );
            }

		}
		else{

            DSPMNode nodeU = new DSPMNode(U);
            nodeU.getNeighbours().add(V);
            graphCache.put(U, nodeU);

			randomWalk(U, R, false);
			
			fip_random_walk(U, U, 1, false);
		}

	}	

	public void randomWalk(Object key, int steps, boolean negative){

		DSPMNode tmpNode = (DSPMNode) graphCache.get(key);

		if (negative)
			tmpNode.setDspmVisits(tmpNode.getDspmVisits() - steps);
		else
			tmpNode.setDspmVisits(tmpNode.getDspmVisits() + steps);

        graphCache.put(key, tmpNode);
        sendVisitDriftIfNeeded(negative ? -steps : steps);

        if ( !tmpNode.getNeighbours().isEmpty() ){

			int ctr = 0;
			for (int i=0; i < steps; i++){
				if ( generator.raw() > Const.EPSILON )
					ctr++;
			}
			if (ctr>0){

				//adjust pending negative steps
				ctr = adjustPendNeg(key, tmpNode, ctr, negative);
				assert(ctr>=0);
				if (ctr==0)
					return;

				if (negative)
					negativeSteps(key, tmpNode, ctr);
				else
					positiveSteps(key, tmpNode, ctr);

			}
		}
	}


	private void positiveSteps(Object key, DSPMNode tmpNode, int ctr){
		TObjectIntHashMap tempMap = new TObjectIntHashMap();

		if (tmpNode.getNeighbours().size() == 1)
			tempMap.put(tmpNode.getNeighbours().toArray()[0], ctr);
		else{

			Object[] neigh = tmpNode.getNeighbours().toArray();
			for (int i=0; i < ctr; i++)
				tempMap.adjustOrPutValue(neigh[unif.nextIntFromTo( 0, neigh.length - 1 )], 1, 1);
		}

		mapUpdate(key, tempMap, tmpNode, false);

		/*if (periodic || init_phase)
			iterateMapAndWalkCloud(tmpNode.getMyCloud(), tempMap, false);
		else{*/
			TObjectIntIterator it = tempMap.iterator();
			for ( int i = tempMap.size(); i-- > 0; ) {
				it.advance();

				/*cm.increaseLW( tmpNode.getMyCloud(), graph.get(it.key()).getMyCloud(), it.value());
				cm.sendMsgs( tmpNode.getMyCloud(), graph.get(it.key()).getMyCloud(), 1);*/
				randomWalk( it.key(), it.value(), false);
			}
		//}

	}

	private void negativeSteps(Object key, DSPMNode tmpNode, int ctr){
		TObjectIntHashMap tempMap = new TObjectIntHashMap();

		if (tmpNode.getNeighbours().size() == 1)
			tempMap.put(tmpNode.getNeighbours().toArray()[0], ctr);
		else{

			int y = countValuesOfMap(tmpNode.getStepChoices());
			assert(y>=ctr);

			if (ctr == y)
				tempMap.putAll(tmpNode.getStepChoices());
			else{
				if ( ctr > Const.MULTIPLIER_NEGSTEPS * y )
					tempListApproach(tempMap, tmpNode, ctr);
				else
					treeSetApproach(tempMap, tmpNode, ctr);
			}
		}

		mapUpdate(key, tempMap, tmpNode, true);

		/*if (periodic || init_phase)
			iterateMapAndWalkCloud(tmpNode.getMyCloud(), tempMap, true);
		else{*/
			TObjectIntIterator it = tempMap.iterator();
			for ( int i = tempMap.size(); i-- > 0; ) {
				it.advance();

				/*cm.increaseLW( tmpNode.getMyCloud(), graph.get(it.key()).getMyCloud(), it.value());
				cm.sendMsgs( tmpNode.getMyCloud(), graph.get(it.key()).getMyCloud(), 1);*/
				randomWalk( it.key(), it.value(), true);
			}
		//}

	}

	private void mapUpdate(Object key, TObjectIntHashMap tempMap, DSPMNode tmpNode, boolean negative){

		TObjectIntIterator it = tempMap.iterator();
		for ( int i = tempMap.size(); i-- > 0; ) {
			it.advance();

			if (negative){
				tmpNode.getStepChoices().adjustValue(it.key(), - it.value());

				assert (tmpNode.getStepChoices().get(it.key())  >= 0 );
				if ( tmpNode.getStepChoices().get(it.key()) == 0 )
					tmpNode.getStepChoices().remove(it.key());
			}
			else
				tmpNode.getStepChoices().adjustOrPutValue(it.key(), it.value(), it.value());
		}
        graphCache.put(key, tmpNode);

    }

	/** This approach will be used when the negative steps to be propagated
	 * are more than: 0.75 * tmpNode.getOutVisits().
	 * The alternative approach in such cases would be very slow,
	 * as it would take us much time to draw ctr *distinct* items out of
	 * tmpNode.getOutVisits()+ctr-1.
	 * Thus, in such cases, we choose to pay the temporary memory's cost +
	 * the garbage collecting time.
	 */
	private void tempListApproach(TObjectIntHashMap tempMap, DSPMNode tmpNode, int ctr){

		if (tmpNode.getNeighbours().size()==1)
			tempMap.put(tmpNode.getNeighbours().toArray()[0], ctr);
		else{

			int i, idx;
            ArrayList tmpOutVisits = new ArrayList();

			TObjectIntIterator it = tmpNode.getStepChoices().iterator();
			for ( i = tmpNode.getStepChoices().size(); i-- > 0; ) {
				it.advance();
				for (int j=0; j < it.value() ; j++)
					tmpOutVisits.add( it.key() );
			}

			for ( i = 0 ; i < ctr ; i ++){
				idx = unif.nextIntFromTo(0, tmpOutVisits.size()-1);
				tempMap.adjustOrPutValue(tmpOutVisits.remove(idx), 1, 1);
			}
		}
	}

	/** Alternative approach when we need a smaller number
	 * of random choices, out of all the outVisits.
	 * Build a sorted set from distinct random numbers, drawn between 0 and outVisits+ctr-1.
	 * Emulate the tempList approach, by traversing the treeSet and the stepChoices Map in parallel.
	 * O(n) cost without having to build the temporary arrayList, composed of all keys of stepChoices,
	 * each existing as many times as each value in the Map.
	 */
	private void treeSetApproach(TObjectIntHashMap tempMap, DSPMNode tmpNode, int ctr){

		TreeSet<Integer> distinctSet = new TreeSet<Integer>();
		int choice, y = countValuesOfMap(tmpNode.getStepChoices());
		while ( distinctSet.size() < ctr ){
			choice = unif.nextIntFromTo(0, y-1);
			distinctSet.add(choice);
		}

		boolean flag = true;
		int q = 0;
		Iterator<Integer> it2 = distinctSet.iterator();
		int entry2 = (int) it2.next();

		TObjectIntIterator it = tmpNode.getStepChoices().iterator();
		do{

			it.advance();
			q += it.value();

			while ( entry2 < q ){

				tempMap.adjustOrPutValue(it.key() , 1, 1);
				if ( it2.hasNext() )
					entry2 = it2.next();
				else{
					flag = false;
					break;
				}
			}

		}while( flag );
	}

	private int adjustPendNeg(Object key, DSPMNode tmpNode, int ctr, boolean negative){
		
		int result;

		if (negative){

			int y = countValuesOfMap(tmpNode.getStepChoices());
			if ( ctr > y ){
				tmpNode.setPend(tmpNode.getPend() + (ctr - y));
                graphCache.put(key, tmpNode);

				result = y;
			}
			else
				result = ctr;
		}
		else{

			if (tmpNode.getPend()!=0){
				if ( ctr >= tmpNode.getPend() ){
					result = ctr - tmpNode.getPend();
					tmpNode.setPend(0);
				}
				else{
					tmpNode.setPend(tmpNode.getPend() - ctr);
					result = 0;
				}
                graphCache.put(key, tmpNode);
            }
			else
				result = ctr;
		}

		assert(result>=0);
		return result;
	}

	public void updateWalks(Object U, Object V) {

		//assert(!init_phase);
		
		DSPMNode nodeU = (DSPMNode) graphCache.get(U);
		int visitsU = nodeU.getDspmVisits();
		boolean negative = visitsU < 0 ? true : false;

		assert(!negative);

		if (nodeU.getNeighbours().isEmpty()){

			nodeU.getNeighbours().add(V);
            graphCache.put(U, nodeU);

			int ctr = 0;
			for (int i=0; i < Math.abs(visitsU); i++){
				if ( generator.raw() > Const.EPSILON )
					ctr++;
			}

			assert(nodeU.getPend() == 0);

			if (ctr>0){

				TObjectIntHashMap tempMap = new TObjectIntHashMap();
				tempMap.put(V, ctr);
				mapUpdate(U, tempMap, nodeU, false);

				/*if (periodic)
					iterateMapAndWalkCloud(nodeU.getMyCloud(), tempMap, false);
				else{*/
					/*cm.increaseLW(nodeU.getMyCloud(), graph.get(V).getMyCloud(), ctr);
					cm.sendMsgs(nodeU.getMyCloud(), graph.get(V).getMyCloud(), 1);*/
					randomWalk( V, ctr, negative );
				//}
			}

		}
		else{

			int ctr = 0, outu = nodeU.getNeighbours().size(), tmp = 0, distinct;
			double prob;
			
			distinct = nodeU.getFip_map().size();
			/*TIntObjectIterator<TreeMap<Integer, Integer>> innerIt = nodeU.getFip_map().iterator();
			while ( innerIt.hasNext() ){
				innerIt.advance();
				tmp += innerIt.value().size();
			}*/

            for (Map.Entry<Object,TreeMap<Integer, Object>> e : nodeU.getFip_map().entrySet() ){
                tmp += e.getValue().size();
            }

            assert(tmp == nodeU.getFipVisits());

			double ret = 1 - ((double) distinct/ (double) tmp);
			prob = ( /*(1-Main.EPSILON) **/ (1 - ret)   )/
					(  outu * (1 - ret) + 1 );

			assert(nodeU.getDspmVisits()>=0);
			int yy = countValuesOfMap(nodeU.getStepChoices());
			for (int i=0; i < yy/*Math.abs(nodeU.getDspmVisits())*/ ; i++){
				if ( generator.raw() < prob )
					ctr++;
			}

			if (ctr>0){

				//adjust pending negative steps
				ctr = adjustPendNeg(U, nodeU, ctr, !negative);
				assert(ctr>=0);

				if (ctr>0)
					negativeSteps(U, nodeU, ctr);

				nodeU.getNeighbours().add(V);
                graphCache.put(U, nodeU);

                //adjust pending negative steps
				ctr = adjustPendNeg(U, nodeU, ctr, negative);
				assert(ctr>=0);
				if (ctr==0)
					return;

				TObjectIntHashMap tempMap = new TObjectIntHashMap();
				tempMap.put(V, ctr);
				mapUpdate(U, tempMap, nodeU, false);

				/*if (periodic)
					iterateMapAndWalkCloud(nodeU.getMyCloud(), tempMap, false);
				else{*/
					/*cm.increaseLW(nodeU.getMyCloud(), graph.get(V).getMyCloud(), ctr);
					cm.sendMsgs(nodeU.getMyCloud(), graph.get(V).getMyCloud(), 1);*/
					randomWalk( V, ctr, negative );
				//}
			}
			else{
				nodeU.getNeighbours().add(V);
                graphCache.put(U, nodeU);
            }
		}
	}

}
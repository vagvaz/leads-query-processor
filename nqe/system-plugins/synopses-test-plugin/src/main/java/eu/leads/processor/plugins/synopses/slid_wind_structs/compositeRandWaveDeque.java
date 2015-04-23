package eu.leads.processor.plugins.synopses.slid_wind_structs;

import cern.jet.random.engine.MersenneTwister;
import eu.leads.processor.plugins.synopses.custom_objs_utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Implements a randomized version of the waves' structure
 * designed to count the number of 1's in a sliding window.
 * The main argument towards using it, is the absence
 * of need of cascading the underlying buckets of the
 * Exponential Histograms' structure, when merging of them is necessary.
 * Relevant publication:
 * "http://home.engineering.iastate.edu/~snt/pubs/tocs04.pdf"
 *
 */
public class compositeRandWaveDeque implements slidingwindow {
	final double delta;
	final int independentInstances;
	public final randWaveDeque[] instances;
	private int lastSyncedTime;
	public void setLastSyncedTime(int t) {
		this.lastSyncedTime=t;
	}
	public int getLastSyncedTime() {
		return this.lastSyncedTime;
	}
	public compositeRandWaveDeque clone() {
		return new compositeRandWaveDeque(this);
	}
	
	public String detailedtoString() {
		return "Levels:"+instances[0].getNumberOfLevels()+  " \t Instances:" + instances.length + "\n" +instances[0];		
	}
	
	public compositeRandWaveDeque(compositeRandWaveDeque c) {
		this.windowSize=c.windowSize;
		this.epsilon=c.epsilon;
		mt = new MersenneTwister(0);//(int)System.currentTimeMillis());
		this.q=c.q;
		this.r=c.r;
		this.delta=c.delta;
		this.independentInstances=c.independentInstances;
		this.instances = new randWaveDeque[independentInstances];
		for (int i=0;i<c.instances.length;i++)
			this.instances[i] = c.instances[i].clone();
	}
	
	public String toString() {
		return this.getStringSummary();
	}
	
	public  int getLevelOffset(TimestampWithRank ts, int numberOfLevels) { // repeats the hashing, returning a number
		int NPrime=(int)Math.pow(2,numberOfLevels);
		int val=(int)Math.abs(ts.rank%NPrime);
		// from 0 to (numberOfLevels-1) with exponentially decreasing probability (except from the last two levels)
		long val2 = Math.abs((q*(int)val)%NPrime+ r)%NPrime;
		if (val2==0) 
			return 0;
		else
			return numberOfLevels - (int)Math.floor(randWaveDeque.log2(val2))-1;
	}

	public void setRandWaveRanks(int rank) {
		for (int instance=0;instance<instances.length;instance++) {
			instances[instance].setRank(rank);
		}
	}
	
	public void setRandWaveLevels(ArrayDeque<TimestampWithRank> levelInterrupts[][]) {
		for (int instance=0;instance<levelInterrupts.length;instance++) {
			for (int level=0;level<levelInterrupts[instance].length;level++) {
				instances[instance].setLevel(level,levelInterrupts[instance][level]);
			}
		}
	}
	public randWaveDeque getInstanceRandWave(int cnt) {
		return instances[cnt];
	}
	public ArrayList<TimestampWithRank> getInstance(int instance) {
		HashMap<Integer, TimestampWithRank> ht = new HashMap();
		for (int level=0;level<instances[instance].getNumberOfLevels();level++) { // for all levels
			for (TimestampWithRank ts:instances[instance].getLevels()[level]) { // for all timestamps of each level
				ht.put((int)ts.getRank(), ts);
			}
		}
		return new ArrayList<TimestampWithRank>(ht.values());
	}
	
	public ArrayDeque<TimestampWithRank>[] getInstanceRaw(int instance) {
		return instances[instance].getLevels();
	}

	public int getRank() {
		return instances[0].getRank();
	}

	final MersenneTwister mt;
	final int q,r;
	
	public static compositeRandWaveDeque mergeRandWaves(compositeRandWaveDeque[] dws, double delta, double epsilon, int windowSize, int numberOfEvents) {
		// now merge
		// START now merge with method 4
		final int eventsPerLevel=(int) Math.ceil((double)randWaveDeque.c/Math.pow(epsilon, 2));
		int totalEvents=0;
//		for (compositeRandWaveDeque eh:dws) {
//			totalEvents+=eh.getRank(); 
//		}
		totalEvents=numberOfEvents;
		compositeRandWaveDeque merged = new compositeRandWaveDeque(delta, epsilon, windowSize, totalEvents, totalEvents);
//		System.err.println("Total events are " + totalEvents);

		//levelInterrupts[instances][levels]
		final int numberOfLevelsInMerged=merged.getInstanceRandWave(0).getNumberOfLevels();
		ArrayList<TimestampWithRank> levelInterrupts[][] = new ArrayList[merged.getNumberOfInstances()][numberOfLevelsInMerged];

		for (int i=0;i<levelInterrupts.length;i++) 
			for (int j=0;j<levelInterrupts[0].length;j++)
				levelInterrupts[i][j]=new ArrayList<TimestampWithRank>(eventsPerLevel);

		for (int instance=0;instance<merged.getNumberOfInstances();instance++) {
			for (compositeRandWaveDeque dw:dws) {
				randWaveDeque rw = dw.getInstanceRandWave(instance);
				ArrayDeque<TimestampWithRank> levels[] = rw.getLevels();
				for (int level=0;level<Math.min(levels.length, numberOfLevelsInMerged);level++) {
					ArrayDeque<TimestampWithRank> singleLevel = levels[level];
//					if (!onlyOnce && singleLevel.size()>0 && singleLevel.getFirst().time<=812236) {
//						System.err.println("ThisLevelIsSufficient " + level + " of size " + singleLevel.size()); onlyOnce=true;
//					}
					if (singleLevel.isEmpty()) // not possible that i have something relevant in a lower level
						break;
					if (level<levels.length-1||levels.length==numberOfLevelsInMerged) 
						levelInterrupts[instance][level].addAll(singleLevel);
					else { // half of it goes to level, and the other half to level+1
						for (TimestampWithRank ts:singleLevel) {
							int levelOffset = merged.getLevelOffset(ts, numberOfLevelsInMerged-level);
							levelInterrupts[instance][level+levelOffset].add(ts);
						}
					}
				}
			}
		}
		// now sort all level interrupts and prune the expired
		for (int instance=0;instance<merged.getNumberOfInstances();instance++) 
		{
			for (int level=0;level<numberOfLevelsInMerged;level++) {
				Collections.sort(levelInterrupts[instance][level]);
				if (levelInterrupts[instance][level].size()>eventsPerLevel) {
					//while (levelInterrupts[instance][level].size()>eventsPerLevel)  levelInterrupts[instance][level].remove(levelInterrupts[instance][level].size()-1);
					ArrayList<TimestampWithRank> tmp= new ArrayList<TimestampWithRank>(eventsPerLevel);
					tmp.addAll(levelInterrupts[instance][level].subList(levelInterrupts[instance][level].size()-eventsPerLevel, levelInterrupts[instance][level].size()));
					levelInterrupts[instance][level]=tmp;
	//				Collections.reverse(levelInterrupts[instance][level]);
				}
				levelInterrupts[instance][level].trimToSize();
			}
		}
		ArrayDeque<TimestampWithRank> levelInterruptsDeque[][] = new ArrayDeque[merged.getNumberOfInstances()][numberOfLevelsInMerged];
		for (int i=0;i<levelInterrupts.length;i++) 
			for (int j=0;j<levelInterrupts[0].length;j++)
				levelInterruptsDeque[i][j]=new ArrayDeque<TimestampWithRank>(levelInterrupts[i][j]);
		
		merged.setRandWaveLevels(levelInterruptsDeque);
		merged.setRandWaveRanks(totalEvents);
		// no expiration check required, since the windows are time-based and not arrival-based

		return merged;
	}

	public String getStringSummary2() {
		return "Independent instances: " + this.independentInstances + " DeltaRW: " + this.delta + " EpsilonRW: " + this.epsilon;
	}
	public String getStringSummary() {
		StringBuilder sb = new StringBuilder();
		randWaveDeque instance = instances[0];
//		for (randWaveDeque instance:instances) 
		{
			for (int level=0;level<instance.getNumberOfLevels();level++) {
				if (instance.getLevels()[level].size()>0)
					sb.append(level + "(" + instance.getLevels()[level].size() + "-" +instance.getLevels()[level].getFirst().toString() + ") ");
				else break;
			}
			sb.append( " LLL ");
			
		}
		return sb.toString();
	}
	double epsilon;
	final int windowSize;
	public compositeRandWaveDeque(double delta, double epsilon, int windowSize, long maxEvents, int expectedElements) {
		this.windowSize=windowSize;
		this.epsilon=epsilon;
		mt = new MersenneTwister(0);//(int)System.currentTimeMillis());
		this.q=Math.abs(mt.nextInt());
		this.r=Math.abs(mt.nextInt());
		this.delta=delta;
		int indInst = (int)(Math.ceil(Math.log(1d/delta)/Math.log(3)));
//		System.err.println("Setting independent instances to 1 for debugging");
//		indInst=1;
		if (indInst%2==0) // then median is no good!
			this.independentInstances=indInst+1;
		else
			this.independentInstances=indInst;
		this.instances = new randWaveDeque[independentInstances];
		for (int i=0;i<this.independentInstances;i++) {
			this.instances[i]=new randWaveDeque(epsilon, windowSize, maxEvents, i, expectedElements);
//			try {Thread.sleep(2);} catch(Exception ignored){};
		}
	}
	
	public int getNumberOfInstances(){
		return independentInstances;
	}
	
	int lastTime=0;
	/**
	 * Function used to modify our structure accordingly
	 * when an event ('1') has been detected
	 * @param t the timestamp of the event
	 */
	public void addAOne(int t) {
		for (int cnt=0;cnt<this.independentInstances;cnt++) {
			instances[cnt].addAOne(t);
		}
		lastTime=t;
	}
	public void addAZero(int t) {
		for (int cnt=0;cnt<this.independentInstances;cnt++) {
			instances[cnt].addAZero(t);
		}
	}

	public void batchUpdate(Stream s) {
		for (Event e:s.getEvents()) {
			if (e.event)
				this.addAOne(e.time);
			else
				this.addAZero(e.time);
		}
	}

	/**
	 * returns a real-estimate estimate provided
	 * by the structure, given the query time as parameter
	 */
	public double getEstimationRealtime(int query) {
		ArrayList<Double>results = new ArrayList<Double>(this.independentInstances);
		for (int cnt=0;cnt<this.independentInstances;cnt++) 
			results.add(instances[cnt].getEstimationRealtime(query));
		Collections.sort(results);
		return results.get(results.size()/2);
	}

	public int getCurrentRealtime() {
		return this.instances[0].currentRealtime;
	}

	// required memory in Kbytes!
	public double getRequiredMemory() {
		double total = 0;
		for (randWaveDeque rw:this.instances) {
			total+=rw.getRequiredMemory();
		}
		return total;
	}	
	
	// required network in Kbytes!
	public double getRequiredNetwork() {
		double total = 0;
		for (randWaveDeque rw:this.instances) {
			total+=rw.getRequiredNetwork();
		}
		return total;
	}
	
	
	double keepInMem=0;
	public void updateByMany(int realtimeNow, float val) {
		double toInsert=keepInMem+val;
		
		while (toInsert>=1) {
			addAOne(realtimeNow);
			toInsert--;
			if (toInsert<1) keepInMem=toInsert;
		}
		if (keepInMem<1e-5) keepInMem=0;
	}

	public void cloneForQuerying() {
	}

	public void removeExpired(int currentTime) {
		for (randWaveDeque r:this.instances) {
			r.removeExpired(currentTime);
		}
	}
	public double getEstimationRange(int range) {
		int startRangeTime = lastSyncedTime-range;		// range will give me the length of the query
		return this.getEstimationRealtime(startRangeTime);
	}
	@Override
	public double getEpsilon() {
		return epsilon;
	}
	@Override
	public int getLastUpdateTime() {
		return 0;
	}
	@Override
	public Pair getEstimationRealtimeWithExpiryTime(int startTime, int queryLength) {
		return null;
	}
	@Override
	public void removeExpiredWithExpiryTime(int startTime) {
	}
}

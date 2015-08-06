package test;

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
import eu.leads.processor.plugins.synopses.custom_objs_utils.IntFunnel;
import eu.leads.processor.plugins.synopses.slid_wind_structs.ExponentialHistogramCircularInt;
import eu.leads.processor.plugins.synopses.slid_wind_structs.compositeRandWaveDeque;
import eu.leads.processor.plugins.synopses.slid_wind_structs.slidingCMSketch;
import eu.leads.processor.plugins.synopses.whole_stream_structs.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;


public class main_test {

	public static void main(String[] args) {

		BloomFilterTest(1000000, 0.01);
		/*ExtendedBloomFilterTest(1000000, 1024<<7, 1, 4);
		CountingBloomFilterTest(1000000, 0.01);
		FMsketchTest(1000000, 5, 4);
		AMSsketchTest(1000000, 0.001, 0.01);*/
		CMsketchTest(1000000, 0.001, 0.001);
		/*ExpHistTest(0.5, 10, 1000, 100);
		RandWavesTest(0.1, 0.5, 10, 1000, 100);
		ECMsketchTest(1000000, 0.01, 0.01, 10000, slidingCMSketch.sliding_window_structures.EC, 1, false);*/
	}

	public static void ECMsketchTest(int insertsNo, double delta, double epsilon,
			int queryLength, slidingCMSketch.sliding_window_structures s,
			int repeat, boolean optimizeForSJ){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());

		slidingCMSketch myecm = new slidingCMSketch(delta, epsilon, queryLength, insertsNo, s, insertsNo, false);

		Normal myNormalDist = new Normal(0, 11, generator);
		HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();

		int ctr = 0;
		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();

			if (i >= insertsNo - queryLength){
				if (myMap.containsKey(tmp))
					myMap.put(tmp, myMap.get(tmp)+1);
				else
					myMap.put(tmp, 1);
				ctr++;
			}

			myecm.add(tmp, i);
		}
		assert (ctr == queryLength);

		long F2 = 0;
		for (Integer i: myMap.values()){
			F2 += Math.pow(i, 2);
		}

		//self-join estimate
		System.out.println("Self-join size: "+F2);
		System.out.println(myecm.getInnerProduct(insertsNo-queryLength));

		//count estimate
		System.out.println("True zero count: "+myMap.get(1));
		System.out.println(myecm.get(1, insertsNo-queryLength));

		//inner product
		slidingCMSketch myecm2 = new slidingCMSketch(delta, epsilon, queryLength, insertsNo, s, insertsNo, false);
		myecm2.setAlphas(myecm.getAlphas());
		myecm2.setBetas(myecm.getBetas());

		myNormalDist = new Normal(0, 9, generator);
		HashMap<Integer, Integer> myMap2 = new HashMap<Integer, Integer>();

		ctr = 0;
		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();

			if (i >= insertsNo - queryLength){
				if (myMap2.containsKey(tmp))
					myMap2.put(tmp, myMap2.get(tmp)+1);
				else
					myMap2.put(tmp, 1);
				ctr++;
			}

			myecm2.add(tmp, i);
		}
		assert (ctr == queryLength);

		//100% accurate inner product
		long innProd = 0;
		for (Entry<Integer, Integer> e: myMap2.entrySet()){
			if (myMap.containsKey(e.getKey())){
				innProd += e.getValue()*myMap.get(e.getKey());
			}
		}

		System.out.println("Accurate inner product: "+innProd);
		System.out.println(myecm.getInnerProductBetweenTwoECMs(insertsNo-queryLength, myecm2));

	}

	public static void RandWavesTest(double delta, double epsilon, int queryLength, int maxEvents, int expectedElems){

		Random rn = new Random();
		compositeRandWaveDeque ex = new compositeRandWaveDeque(delta, epsilon, queryLength, maxEvents, expectedElems);
		boolean[] events = new boolean[expectedElems];
		for (int i=0;i<expectedElems;i++) {
			if (rn.nextBoolean()) {
				events[i] = true;
				ex.addAOne(i);
			}
			int realAnswer=0;
			if (i>queryLength) {
				for (int j=i;j>=i-queryLength;j--) if (events[j]) realAnswer++;
				System.err.print("Time " + i + "  startTime  "+ (i-queryLength) + " Real answer " + realAnswer);
				System.err.println("  I=" + i + " " + ex.getEstimationRealtime(i-queryLength) );
			}
		}
	}

	public static void ExpHistTest(double epsilon, int queryLength, int maxEvents, int eventsFlowing){

		Random rn = new Random();
		ExponentialHistogramCircularInt ex = new ExponentialHistogramCircularInt(epsilon, queryLength, maxEvents);
		boolean[] events = new boolean[eventsFlowing];
		for (int i=0;i<eventsFlowing;i++) {
			if (rn.nextBoolean()) {
				events[i] = true;
				ex.addAOne(i);
			}
			int realAnswer=0;
			if (i>queryLength) {
				for (int j=i;j>=i-queryLength;j--) if (events[j]) realAnswer++;
				System.err.print("Time " + i + "  startTime  "+ (i-queryLength) + " Real answer " + realAnswer);
				System.err.println("  I=" + i + " " + ex.getEstimationRealtimeWithExpiryTime(i-queryLength, queryLength) );
			}
		}
	}

	public static void CMsketchTest(int insertsNo, double delta, double epsilon){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());

		CM_Sketch<Integer> mycm = new CM_Sketch<Integer>
		(IntFunnel.INSTANCE, delta, epsilon);

		Normal myNormalDist = new Normal(0, 100, generator);
		HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();

			if (myMap.containsKey(tmp))
				myMap.put(tmp, myMap.get(tmp)+1);
			else
				myMap.put(tmp, 1);

			mycm.put(new Integer(tmp));
		}

		long F2 = 0;
		for (Integer i: myMap.values()){
			F2 += Math.pow(i, 2);
		}

		//self-join estimate
		System.out.println(F2);
		System.out.println(mycm.estimateF2());

		//count estimate
		System.out.println("True zero count: "+myMap.get(0));
		System.out.println(mycm.estimateCount(new Integer(0)));

		//inner product
		CM_Sketch<Integer> mycm2 = new CM_Sketch<Integer>
		(IntFunnel.INSTANCE, delta, epsilon);
		mycm2.setRandomSeeds(mycm.getRandomSeeds());

		myNormalDist = new Normal(100, 100, generator);
		HashMap<Integer, Integer> myMap2 = new HashMap<Integer, Integer>();

		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();
			if (myMap2.containsKey(tmp))
				myMap2.put(tmp, myMap2.get(tmp)+1);
			else
				myMap2.put(tmp, 1);

			mycm2.put(new Integer(tmp));
		}

		//100% accurate inner product
		long innProd = 0;
		for (Entry<Integer, Integer> e: myMap2.entrySet()){

			if (myMap.containsKey(e.getKey())){
				innProd += e.getValue()*myMap.get(e.getKey());
			}

		}

		System.out.println("Accurate inner product: "+innProd);
		System.out.println(mycm.innerProduct(mycm2));

	}


	public static void AMSsketchTest(int insertsNo, double delta, double epsilon){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());

		AMS_Sketch<Integer> myams = new AMS_Sketch<Integer>
		(IntFunnel.INSTANCE, delta, epsilon);

		Normal myNormalDist = new Normal(0, 10000, generator);
		HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();
			if (myMap.containsKey(tmp))
				myMap.put(tmp, myMap.get(tmp)+1);
			else
				myMap.put(tmp, 1);

			myams.put(new Integer(tmp));
		}

		long F2 = 0;
		for (Integer i: myMap.values()){
			F2 += Math.pow(i, 2);
		}

		//self-join estimate
		System.out.println(F2);
		System.out.println(myams.estimateF2());

		//count estimate
		System.out.println(myams.estimateCount(new Integer(0)));

		//inner product
		AMS_Sketch<Integer> myams2 = new AMS_Sketch<Integer>
		(IntFunnel.INSTANCE, delta, epsilon);
		myams2.setRandomSeeds(myams.getRandomSeeds());

		myNormalDist = new Normal(100, 10000, generator);
		HashMap<Integer, Integer> myMap2 = new HashMap<Integer, Integer>();

		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();
			if (myMap2.containsKey(tmp))
				myMap2.put(tmp, myMap2.get(tmp)+1);
			else
				myMap2.put(tmp, 1);

			myams2.put(new Integer(tmp));
		}

		//100% accurate inner product
		long innProd = 0;
		for (Entry<Integer, Integer> e: myMap2.entrySet()){

			if (myMap.containsKey(e.getKey())){
				innProd += e.getValue()*myMap.get(e.getKey());
			}

		}

		System.out.println("Accurate inner product: "+innProd);
		System.out.println(myams.innerProduct(myams2));

	}


	public static void FMsketchTest(int insertsNo, int numHashGroups, int numHashFunctionsInHashGroup){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());

		FM_Sketch<Integer> myfm = new FM_Sketch<Integer>(IntFunnel.INSTANCE,
				numHashGroups, numHashFunctionsInHashGroup);

		Normal myNormalDist = new Normal(0, 10000, generator);

		HashSet<Integer> mySet = new HashSet<Integer>();
		for (int i = 0; i < insertsNo; i++){
			int tmp = myNormalDist.nextInt();
			mySet.add(tmp);
			myfm.put(new Integer(tmp));
		}

		System.out.println(myfm.distinct_values_estimate());
		System.out.println(mySet.size());

	}


	public static void BloomFilterTest(int expectedInserts, double fpp){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());
		BloomFilter<Integer> mybf = new BloomFilter<Integer>(IntFunnel.INSTANCE, expectedInserts, fpp, generator.nextInt());

		HashSet<Integer> mySet = new HashSet<Integer>();
		for (int i = 0; i < expectedInserts; i++){

			int tmp = generator.nextInt();
			mySet.add(tmp);
			mybf.put(new Integer(tmp));

		}

		System.out.println("Distinct values added: "+mySet.size());
		System.out.println("Estimate of distinct values: "+mybf.estimateCardinality());

		HashSet<Integer> mySet2 = new HashSet<Integer>();

		BloomFilter<Integer> mybf2 = new BloomFilter<Integer>(IntFunnel.INSTANCE, expectedInserts, fpp, mybf.getSeed());

		for (int i = 0; i < expectedInserts; i++){

			int tmp = generator.nextInt();
			while (mySet.contains(tmp))
				tmp = generator.nextInt();

			mySet2.add(tmp);
			mybf2.put(new Integer(tmp));
		}

		//union of bloom filters test
		mybf2.uniteMe(mybf);
		int falsePositives = 0;
		for (int i = 0; i < expectedInserts; i++){

			int tmp = generator.nextInt();
			while (mySet.contains(tmp) || mySet2.contains(tmp))
				tmp = generator.nextInt();

			if (mybf2.mightContain(tmp))
				falsePositives++;
		}
		System.out.println("Union of BloomFilters: "
				+ "Percentage of False Positives: "
				+ (double) falsePositives/ (double) expectedInserts);
		System.out.println("Estimated fpp of unitedBF: "+mybf2.fppOfUnitedOrIntersectedBF());

		//standard test: fpp
		falsePositives = 0;
		for (Integer a : mySet2){
			if (mybf.mightContain(a))
				falsePositives++;
		}
		System.out.println("BloomFilter: "
				+ "Percentage of False Positives: "
				+ (double) falsePositives/ (double) expectedInserts);
	}

	public static void ExtendedBloomFilterTest(int expectedInserts, int length, int hashFunctions, int numberOfBlocks){
		BlockPartitionedBloomFilter<Integer> bpbf =
				new BlockPartitionedBloomFilter<Integer>(IntFunnel.INSTANCE, length, hashFunctions, numberOfBlocks);

		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());
		Normal myNormalDist = new Normal(0, 1000, generator);
		HashSet<Integer> mySet = new HashSet<Integer>();
		for (int i = 0; i < expectedInserts; i++){

			int tmp = myNormalDist.nextInt();
			mySet.add(tmp);
			bpbf.put(new Integer(tmp));

		}

		System.out.println("Distinct values added: "+mySet.size());
		System.out.println("Estimate of distinct values: "+bpbf.estimateCardinality());

		HashSet<Integer> mySet2 = new HashSet<Integer>();
		for (int i = 0; i < expectedInserts; i++){

			int tmp = generator.nextInt();
			while (mySet.contains(tmp))
				tmp = generator.nextInt();

			mySet2.add(tmp);
		}

		//standard test: fpp
		int falsePositives = 0;
		for (Integer a : mySet2){
			if (bpbf.mightContain(a))
				falsePositives++;
		}
		System.out.println("BloomFilter: "
				+ "Percentage of False Positives: "
				+ (double) falsePositives/ (double) expectedInserts);

	}
	
	public static void CountingBloomFilterTest(int expectedInserts, double fpp){
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());
		CountingBloomFilter<Integer> mybf = new CountingBloomFilter<Integer>(IntFunnel.INSTANCE, expectedInserts, fpp, generator.nextInt());
		Normal myNormalDist = new Normal(0, 100000, generator);

		HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < expectedInserts; i++){

			int tmp = myNormalDist.nextInt();
			if (myMap.containsKey(tmp))
				myMap.put(tmp, myMap.get(tmp)+1);
			else
				myMap.put(tmp, 1);

			mybf.incr_decr(new Integer(tmp), true);
		}

		System.out.println("Distinct values added: "+myMap.size());
		System.out.println("Estimate of distinct values: "+mybf.estimateCardinality());

		int falsePositives = 0;
		for (int i = 0; i < expectedInserts; i++){
			int tmp = generator.nextInt();
			
			while (myMap.containsKey(tmp))
				tmp = generator.nextInt();

			if (mybf.mightContain(tmp))
				falsePositives++;
		}

		System.out.println("BloomFilter: "
				+ "Percentage of False Positives: "
				+ (double) falsePositives/ (double) expectedInserts);
	}

}

package eu.leads.processor.plugins.synopses.whole_stream_structs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Random;

import eu.leads.processor.plugins.synopses.custom_objs_utils.BitArray;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cern.jet.random.engine.RandomEngine;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;


/**
 * Partial use of code from:
 * https://github.com/rbhide0/Columbus/blob/master/src/main/java/rbhide0/streaming/algorithm/FlajoletMartin.java
 * 
 * Functionality: distinct element counting.
 */
public class FM_Sketch<T> {

	/**
	 * Parameter of the FM sketch
	 */
	private static final double PHI = 0.77351D;
	/**
	 * default bitmap size L: 64 bit.
	 * We need that 2^L > n, so 2^64 is quite large
	 * for most purposes.
	 */
	private static final int bitmapSize = 64;

	/** The bit set of the BloomFilter (not necessarily power of 2!)*/
	private BitArray[][] bitarrays;
	/** The funnel to translate Ts to bytes */
	private final Funnel<T> funnel;
	/**
	 * Number of groups of hash functions that will be used.
	 */
	private int numHashGroups;
	/**
	 * Number of hash functions in each group that will be used.
	 */
	private int numHashFunctionsInHashGroup;

	private RandomEngine myRandomGen;
	private final int randomInitSeed;

	public FM_Sketch(Funnel<T> funnel, int numHashGroups, int numHashFunctionsInHashGroup) {

		Random r = new Random();
		randomInitSeed = r.nextInt();

		checkArgument(numHashGroups > 0, "numHashGroups must be > 0",
				numHashGroups);
		checkArgument(numHashFunctionsInHashGroup > 0, "numHashFunctionsInHashGroup must be > 0",
				numHashFunctionsInHashGroup);

		this.numHashGroups = numHashGroups;
		this.numHashFunctionsInHashGroup = numHashFunctionsInHashGroup;

		bitarrays = new BitArray[numHashGroups][numHashFunctionsInHashGroup];
		for (int i=0 ; i < numHashGroups; i++){
			for (int j=0; j < numHashFunctionsInHashGroup; j++){
				bitarrays[i][j] = new BitArray(bitmapSize);
			}
		}

		this.funnel = checkNotNull(funnel);
	}

	/**
	 * Placement of an object into our bloom filter.
	 * We consider our result as the median of the averages of 
	 * the results of different hash functions.
	 * These functions remain the same for different objects,
	 * because of seeding them with the same random seed each time.
	 * 
	 * @param object object of the stream we are currently considering
	 * @return if something changed at all in our bitmaps
	 */
	public boolean put(T object)
	{
		myRandomGen = new cern.jet.random.engine.MersenneTwister64(randomInitSeed);

		long hash1;
		int trailing_zeros_of_hashed;
		boolean bitsChanged = false;
		
		for (int i = 0; i < numHashGroups; i++) {
			for (int j = 0; j < numHashFunctionsInHashGroup; j++) {
				hash1 = Hashing.murmur3_128(myRandomGen.nextInt()).hashObject(object, funnel).asLong();
				trailing_zeros_of_hashed = rho(hash1);
				bitsChanged |= bitarrays[i][j].set(trailing_zeros_of_hashed);
			}
		}
		return bitsChanged;
	}

	public double distinct_values_estimate(){

		ArrayList<Double> averageR = new ArrayList<Double>();
		for (int i=0 ; i < numHashGroups; i++){
			int sumR = 0;
			for (int j=0; j < numHashFunctionsInHashGroup; j++){
				sumR += getFirstZeroBit(bitarrays[i][j]);
			}
			averageR.add( (double) sumR / (double) numHashFunctionsInHashGroup );
		}
		return Math.pow(2, getMedian(averageR)) / PHI;
	}

	private double getMedian(ArrayList<Double> list){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double d : list)
			stats.addValue(d);
		return stats.getPercentile(50);
	}

	/**
	 * 
	 * @param v hashed value for which we are searching
	 * the number of trailing zeros
	 * @return trailing zeros in v
	 */
	private int rho(long v) {
		int rho = 0;
		for (int i=0; i<bitmapSize; i++){ // size of long=64 bits.
			if ((v & 0x01) == 0){
				v = v >> 1;
				rho++;
			}
			else
				break;
		}
		return rho == bitmapSize ? 0 : rho;
	}

	/**
	 * 
	 * @param b in use
	 * @return fringe of 0/1s (index of rightmost zero) which is
	 * around log(#distinct_values)
	 */
	private int getFirstZeroBit(BitArray b) {
		for (int i=0; i<bitmapSize; i++) {
			if (!b.get(i)){
				return i;
			}
		}
		return bitmapSize;
	}

}

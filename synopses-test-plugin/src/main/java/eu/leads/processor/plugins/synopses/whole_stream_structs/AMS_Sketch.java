package eu.leads.processor.plugins.synopses.whole_stream_structs;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;

import cern.jet.random.engine.RandomEngine;

/**
 * Partially migrated from:
 * https://github.com/mayconbordin/streaminer
 * The main aspects of this method can be read
 * from "http://people.cs.umass.edu/~mcgregor/711S12/sketches1.pdf".
 * Its main idea is to use a number of hash functions in order to hash
 * streaming data, and then use additional hash functions of
 * Rademacher variables (+1/-1), which are multiplied by the
 * frequency amount we are increasing each bucket.
 * Provides *unbiased* estimators (e.g. F2 moment).
 */
public class AMS_Sketch<T> {

	private int depth;
	private int buckets;
	private int count = 0;
	private int[] counts;
	private int[][] randomSeeds;

	/** The funnel to translate Ts to bytes */
	private final Funnel<T> funnel;

	private RandomEngine myRandomGen;

	public AMS_Sketch(Funnel<T> funnel, double delta, double epsilon) {

		depth = (int) Math.ceil(8 * Math.log(1d/delta));
		buckets = (int) Math.ceil(4d/Math.pow(epsilon, 2));
		
		randomSeeds = new int[depth][2];
		myRandomGen = new cern.jet.random.engine.MersenneTwister64();

		for (int i = 0; i < depth ; i++){
			for (int j = 0 ; j < 2 ; j ++){
				randomSeeds[i][j] = myRandomGen.nextInt();
			}
		}
		
		counts = new int[buckets*depth];
		this.funnel = checkNotNull(funnel);
	}

	public int[][] getRandomSeeds() {
		return randomSeeds;
	}

	public void setRandomSeeds(int[][] randomSeeds) {
		this.randomSeeds = randomSeeds;
	}

	public void put(T item) {
		put(item, 1);
	}

	/**
	 * Puts an item of generic type T into the sketch structure.
	 * Hashing with a different hash function for each level (depth)
	 * of the sketch and use of the Rademacher (+1/-1) hash
	 * to add +1/-1 * incrementCount to each respective bucket.
	 * 
	 * @param item to be put into our sketch
	 * @param incrementCount of frequencies to add
	 */
	public void put(T item, long incrementCount) {
		int offset = 0;
		int hash, mult;
		count += incrementCount;

		for (int j=0; j<depth; j++){

			hash = Hashing.murmur3_128(randomSeeds[j][0]).hashObject(item, funnel).asInt();
			// Flip all the bits if it's negative (guaranteed positive number)
			if (hash < 0)
				hash = ~hash;
			hash = hash % buckets;
			mult = Hashing.murmur3_128(randomSeeds[j][1]).hashObject(item, funnel).asInt();
			if ((mult&1)==1)
				counts[offset+hash] += incrementCount;
			else
				counts[offset+hash]-= incrementCount;

			offset += buckets;
		}

	}

	/**
	 * 
	 * @return returns the estimate for the self-join
	 */
	public long estimateF2() {
		// estimate the F2 moment of the vector (sum of squares)
		DescriptiveStatistics stats = new DescriptiveStatistics();
		int r = 0;
		long z;

		for (int i=0; i<depth; i++) {
			z=0;
			for (int j=0; j<buckets; j++) {
				z += counts[r] * counts[r];
				r++;
			}
			stats.addValue(z);
		}

		return (long) stats.getPercentile(50);
	}
	
	/**
	 * 
	 * @param item for which we request an estimate of its current frequency
	 * @return returns the requested frequency estimate according to our
	 * sketch structure
	 */
	public long estimateCount(T item) {
		int offset = 0;
		int hash, mult;
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (int j=0; j<depth; j++){

			hash = Hashing.murmur3_128(randomSeeds[j][0]).hashObject(item, funnel).asInt();
			// Flip all the bits if it's negative (guaranteed positive number)
			if (hash < 0)
				hash = ~hash;
			hash = hash % buckets;
			mult = Hashing.murmur3_128(randomSeeds[j][1]).hashObject(item, funnel).asInt();
			if ((mult&1)==1)
				stats.addValue(counts[offset+hash]);
			else
				stats.addValue(-counts[offset+hash]);

			offset += buckets;
		}
		
		return (long) stats.getPercentile(50);
    }
	
	/**
	 * 
	 * @param b: calculation of inner product between data represented as this sketch object
	 * and sketch b
	 * @return their inner product
	 */
	public long innerProduct(AMS_Sketch<T> b){

        if (!this.compareTo(b))
        	return 0;

		DescriptiveStatistics stats = new DescriptiveStatistics();
        int r=0;
        long z;
		for (int i=0; i<depth; i++){
			z=0;
			for (int j=0; j<buckets; j++) {
                z += counts[r] * b.counts[r];
                r++;
            }
			stats.addValue(z);
		}
		
		return (long) stats.getPercentile(50);

	}
	
	/**
	 * checks for compatibility between two AMS sketches
	 */
	private boolean compareTo(AMS_Sketch<T> o) {
        if (buckets != o.buckets)
            return false;
        
        if (depth != o.depth)
            return false;
        
        for (int i = 0; i < depth ; i++){
			for (int j = 0 ; j < 2 ; j ++){
				if (randomSeeds[i][j] != o.randomSeeds[i][j])
					return false;
			}
		}
        
        return true;
    }
}

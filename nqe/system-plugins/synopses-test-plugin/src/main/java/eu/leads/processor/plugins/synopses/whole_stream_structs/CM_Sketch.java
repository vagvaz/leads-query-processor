package eu.leads.processor.plugins.synopses.whole_stream_structs;

import cern.jet.random.engine.RandomEngine;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class shares many commonalities with AMS_Sketch class.
 * Its main difference (as happens with the underlying data structures)
 * is the estimation of the desired amounts (e.g. self-join size)
 * through the minimum amount of all the hash functions used.
 * Additionally, we do not multiply by the Rademacher hash +1/-1,
 * as CM sketch is an increment-only data structure (produces
 * a *biased* over-estimate of the desired amounts).
 */
public class CM_Sketch<T> {

	private int depth;
	private int buckets;
	private int count = 0;
	private int[] counts;
	private int[] randomSeeds;

	/** The funnel to translate Ts to bytes */
	private final Funnel<T> funnel;

	private RandomEngine myRandomGen;

	public CM_Sketch(Funnel<T> funnel, double delta, double epsilon) {

		depth = (int) Math.ceil(Math.log(1d/delta));
		buckets = (int) Math.ceil(Math.E/epsilon);

		randomSeeds = new int[depth];
		myRandomGen = new cern.jet.random.engine.MersenneTwister64();

		for (int i = 0; i < depth ; i++){
			randomSeeds[i] = myRandomGen.nextInt();
		}

		counts = new int[buckets*depth];
		this.funnel = checkNotNull(funnel);
	}

	public int[] getRandomSeeds() {
		return randomSeeds;
	}

	public void setRandomSeeds(int[] randomSeeds) {
		this.randomSeeds = randomSeeds;
	}

	public void put(T item) {
		put(item, 1);
	}

	/**
	 * Puts an item of generic type T into the sketch structure.
	 * Hashing with a different hash function for each level (depth)
	 * 
	 * @param item to be put into our sketch
	 * @param incrementCount of frequencies to add
	 */
	public void put(T item, long incrementCount) {
		int offset = 0;
		int hash;
		count += incrementCount;

		for (int j=0; j<depth; j++){

			hash = Hashing.murmur3_128(randomSeeds[j]).hashObject(item, funnel).asInt();
			// Flip all the bits if it's negative (guaranteed positive number)
			if (hash < 0)
				hash = ~hash;
			hash = hash % buckets;
			counts[offset+hash] += incrementCount;
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

		return (long) stats.getMin();
	}

	/**
	 * 
	 * @param item for which we request an estimate of its current frequency
	 * @return returns the requested frequency estimate according to our
	 * sketch structure
	 */
	public long estimateCount(T item) {
		int offset = 0;
		int hash;
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int j=0; j<depth; j++){

			hash = Hashing.murmur3_128(randomSeeds[j]).hashObject(item, funnel).asInt();
			// Flip all the bits if it's negative (guaranteed positive number)
			if (hash < 0)
				hash = ~hash;
			hash = hash % buckets;

			stats.addValue(counts[offset+hash]);

			offset += buckets;
		}

		return (long) stats.getMin();
	}

	/**
	 * 
	 * @param b: calculation of inner product between data represented as this sketch object
	 * and sketch b
	 * @return their inner product
	 */
	public long innerProduct(CM_Sketch<T> b){

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

		return (long) stats.getMin();

	}

	/**
	 * checks for compatibility between two CM sketches
	 */
	private boolean compareTo(CM_Sketch<T> o) {
		if (buckets != o.buckets)
			return false;

		if (depth != o.depth)
			return false;

		for (int i = 0; i < depth ; i++){
			if (randomSeeds[i] != o.randomSeeds[i])
				return false;
		}

		return true;
	}
}

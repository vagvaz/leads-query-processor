package eu.leads.processor.plugins.synopses.whole_stream_structs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import eu.leads.processor.plugins.synopses.custom_objs_utils.BitArrayCount;


public class CountingBloomFilter<T> {

	/** The bit set of the BloomFilter (not necessarily power of 2!)*/
	private BitArrayCount bits;

	/** Number of hashes per element */
	private int numHashFunctions;

	/** The funnel to translate Ts to bytes */
	private final Funnel<T> funnel;

	/** seed used for the hash functions of this bloom filter */
	private final int seed;
	
	private long countersNum;

	public BitArrayCount getBits() {
		return bits;
	}

	public void setBits(BitArrayCount bits) {
		this.bits = bits;
	}

	public CountingBloomFilter(Funnel<T> funnel, int expectedInsertions, double fpp, int randomSeed){
		seed = randomSeed;

		checkArgument(expectedInsertions >= 0, "Expected insertions (%s) must be >= 0",
				expectedInsertions);
		checkArgument(fpp > 0.0, "False positive probability (%s) must be > 0.0", fpp);
		checkArgument(fpp < 1.0, "False positive probability (%s) must be < 1.0", fpp);

		if (expectedInsertions == 0) {
			expectedInsertions = 1;
		}

		long numBits = optimalNumOfBits(expectedInsertions, fpp);
		countersNum = numBits;
		
		int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
		checkArgument(numHashFunctions > 0,
				"numHashFunctions (%s) must be > 0", numHashFunctions);
		checkArgument(numHashFunctions <= 255,
				"numHashFunctions (%s) must be <= 255", numHashFunctions);

		bits = new BitArrayCount(numBits);

		this.numHashFunctions = numHashFunctions;
		this.funnel = checkNotNull(funnel);
	}

	private long optimalNumOfBits(long n, double p) {
		if (p == 0) {
			p = Double.MIN_VALUE;
		}
		return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
	}
	private int optimalNumOfHashFunctions(long n, long m) {
		return Math.max(1, (int) Math.round(m / n * Math.log(2)));
	}

	public int getSeed(){
		return seed;
	}

	public int getNumHashFunctions() {
		return numHashFunctions;
	}

	public void setNumHashFunctions(int numHashFunctions) {
		this.numHashFunctions = numHashFunctions;
	}

	/**
	 * @param object the object we want to increment or decrement its value in the structure
	 */
	public void incr_decr(T object, boolean incr) 
	{
		long hash64 = Hashing.murmur3_128(seed).hashObject(object, funnel).asLong();
		int hash1 = (int) hash64;
		int hash2 = (int) (hash64 >>> 32);

		for (int i = 1; i <= numHashFunctions; i++) {
			int combinedHash = hash1 + (i * hash2);
			// Flip all the bits if it's negative (guaranteed positive number)
			if (combinedHash < 0) {
				combinedHash = ~combinedHash;
			}
			bits.incr_decr(combinedHash % countersNum, incr);
		}
	}

	/**
	 * 
	 * @param object for which we query our structure
	 * @return a boolean variable indicating whether this object
	 * may be contained in the structure (as concerns a Bloom Filter,
	 * false positives may exist, but false negatives may not)
	 */
	public boolean mightContain(T object) {
		long hash64 = Hashing.murmur3_128(seed).hashObject(object, funnel).asLong();
		int hash1 = (int) hash64;
		int hash2 = (int) (hash64 >>> 32);

		for (int i = 1; i <= numHashFunctions; i++) {
			int combinedHash = hash1 + (i * hash2);
			// Flip all the bits if it's negative (guaranteed positive number)
			if (combinedHash < 0) {
				combinedHash = ~combinedHash;
			}
			if (bits.getCounter(combinedHash % countersNum)==0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Provides a distinct values' estimate,
	 * relevant publication:
	 * "http://www.softnet.tuc.gr/~papapetrou/publications/Bloomfilters-DAPD.pdf"
	 * 3rd equation from the paper.
	 * 
	 * @return the cardinality estimate of distinct values
	 * of our Bloom Filter structure
	 */
	public double estimateCardinality() {
		double m = countersNum,
				i = bits.getBitCount(),
				k = numHashFunctions;
		return (Math.log(1d - i / m) / (k * Math.log(1d - 1d / m)));
	}

	/**
	 * sets this bloom filter as the union of itself and the @param other
	 * @param other the 2nd bloom filter contributing to the result
	 */
	/*public void uniteMe(CountingBloomFilter<T> other){
		assert(seed == other.seed &&
				numHashFunctions == other.numHashFunctions);
		bits.putAll(other.bits);
	}

	*//**
	 * sets this bloom filter as the intersection of itself and the @param other
	 * @param other the 2nd bloom filter contributing to the result
	 *//*
	public void intersectMe(CountingBloomFilter<T> other){
		assert(seed == other.seed &&
				numHashFunctions == other.numHashFunctions);
		bits.intersect(other.bits);
	}

	*//**
	 * 
	 * @return the false positive probability estimate of a bloom filter
	 * resulted as either a union or an intersection of two bloom filters
	 *//*
	public double fppOfUnitedOrIntersectedBF(){
		return Math.pow((double) bits.bitCount() / (double) bits.bitSize(), numHashFunctions);
	}*/
}

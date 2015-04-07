package eu.leads.processor.plugins.synopses.custom_objs_utils;

import static com.google.common.base.Preconditions.checkArgument;

//Note: We use this instead of java.util.BitSet because we need access to the long[] data field
/**
 * Partial migration from Google's Guava library,
 * extended to enable mapping of long numbers to counters,
 * instead of single bits.
 */
public abstract class BitArrayAbstract {

	final long[] data;
	long bitCount;

	// Used by serialization
	protected BitArrayAbstract(long[] data) {
		checkArgument(data.length > 0, "data length is zero!");
		this.data = data;
		this.bitCount = countNumOfOnes();
	}
	
	public long getBitCount() {
		return bitCount;
	}

	public void setBitCount(long bitCount) {
		this.bitCount = bitCount;
	}
	
	protected abstract long countNumOfOnes();
}

package eu.leads.processor.plugins.synopses.custom_objs_utils;

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

import java.math.RoundingMode;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class BitArray extends BitArrayAbstract{
	
	public BitArray(long bits) {
		super(new long[Ints.checkedCast(LongMath.divide(bits, 64, RoundingMode.CEILING))]);
	}
	
	protected long countNumOfOnes(){
		long bitCount = 0;
		for (long value : data) {
			bitCount += Long.bitCount(value);
		}
		return bitCount;
	}
	
	/** Returns true if the bit changed value. */
	public boolean set(long index) {
		if (!get(index)) {
			data[(int) (index >>> 6)] |= (1L << index);
			bitCount++;
			return true;
		}
		return false;
	}

	public boolean get(long index) {
		return (data[(int) (index >>> 6)] & (1L << index)) != 0;
	}
	
	/** Number of bits */
	public long bitSize() {
		return (long) data.length * Long.SIZE;
	}

	/*public BitArray copy() {
		return new BitArray(data.clone());
	}*/

	/** Combines the two BitArrays using bitwise OR. */
	public void putAll(BitArray array) {
		checkArgument(data.length == array.data.length,
				"BitArrays must be of equal length (%s != %s)", data.length, array.data.length);
		bitCount = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] |= array.data[i];
			bitCount += Long.bitCount(data[i]);
		}
	}

	/** Intersection of two BitArrays using bitwise AND. */
	public void intersect(BitArray array) {
		checkArgument(data.length == array.data.length,
				"BitArrays must be of equal length (%s != %s)", data.length, array.data.length);
		bitCount = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i] & array.data[i];
			bitCount += Long.bitCount(data[i]);
		}
	}

	@Override public boolean equals(Object o) {
		if (o instanceof BitArray) {
			BitArray bitArray = (BitArray) o;
			return Arrays.equals(data, bitArray.data);
		}
		return false;
	}

	@Override public int hashCode() {
		return Arrays.hashCode(data);
	}
}

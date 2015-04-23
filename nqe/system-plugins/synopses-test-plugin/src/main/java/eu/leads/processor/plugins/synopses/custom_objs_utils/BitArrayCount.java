package eu.leads.processor.plugins.synopses.custom_objs_utils;

import java.math.RoundingMode;

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

public class BitArrayCount extends BitArrayAbstract {

	/** We are using 4bit buckets in the case of counting BFs, so each bucket can count to 15 */
	private final static long BUCKET_MAX_VALUE = 15;
	
	public BitArrayCount(long bits) {
		super(new long[Ints.checkedCast(LongMath.divide(bits, 16, RoundingMode.CEILING))]);
	}
	
	protected long countNumOfOnes(){
		long bitCount = 0;
		long tmp,tmp2;
		for (long value : data) {
			tmp = value;
			for (int i=0; i < BUCKET_MAX_VALUE ; i++){
				tmp2 = tmp & 0x0f;
				if (tmp2!=0)
					bitCount++;
				tmp = tmp >> 4;
			}
		}
		return bitCount;
	}
	
	/** used only for the underlying bitarray of the counting bloom fliter */
	public void incr_decr(long index, boolean incr){

		long wordNum = index >> 4;          // div 16
		long bucketShift = (index & 0x0f) << 2;  // (mod 16) * 4

		long bucketMask = 15L << bucketShift;
		long bucketValue = (data[(int) wordNum] & bucketMask) >>> bucketShift;
		
		if (incr){
			if(bucketValue < BUCKET_MAX_VALUE) {
				if (bucketValue == 0)
					bitCount++;
				data[(int) wordNum] = (data[(int) wordNum] & ~bucketMask) | ((bucketValue + 1) << bucketShift);
			}
		}
		else{
			// only decrement if the count in the bucket is between 0 and BUCKET_MAX_VALUE
		     if(bucketValue >= 1 && bucketValue < BUCKET_MAX_VALUE) {
		    	 if (bucketValue == 1)
						bitCount--;
				data[(int) wordNum] = (data[(int) wordNum] & ~bucketMask) | ((bucketValue - 1) << bucketShift);
		     }
		}
	}
	
	public int getCounter(long index){
		
		long wordNum = index >> 4;          // div 16
		long bucketShift = (index & 0x0f) << 2;  // (mod 16) * 4

		long bucketMask = 15L << bucketShift;
		return (int) ((data[(int) wordNum] & bucketMask) >>> bucketShift);
	}
}

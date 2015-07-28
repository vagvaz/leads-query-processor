package eu.leads.processor.plugins.synopses.whole_stream_structs;

import com.google.common.hash.Funnel;

import java.util.Random;

public class BlockPartitionedBloomFilter<T> {
	BloomFilter<T>[] blocks;

	public BlockPartitionedBloomFilter(Funnel<T> funnel, int length, int hashFunctions, int numberOfBlocks) {
		Random r = new Random();
		blocks = new BloomFilter[numberOfBlocks];
		for (int cnt=0;cnt<numberOfBlocks;cnt++)
			blocks[cnt] = new BloomFilter(funnel, length, hashFunctions, r.nextInt());		
	}
	
	/*public static void testBlockPartitionedBF () {
		BlockPartitionedBloomFilter bpbf = new BlockPartitionedBloomFilter(1024<<7, 1, 16);
		for (int cnt=0;cnt<1000;cnt++)  {
			bpbf.addInt(cnt);
			System.err.println(bpbf.estimateRangeAndCardinality(0.95));
		}
		System.err.println("Estimated true bits is " + 
				bpbf.estimateNumberOfTrueBits(1000) +
				" and actual true bits is " + bpbf.countTrueBits());
		System.err.println("prob for 900,1100 is " + 
				bpbf.estimateProbabilityInCardinalityRange(900, 1100));
	
	}*/
	
	public int getNumberOfBlocks() {
		return blocks.length;
	}
	
	public long getLength() {
		return blocks[0].getBits().bitSize();
	}
	
	public int getHashes() {
		return blocks[0].getNumHashFunctions();
	}
	
	public void put(T obj) {
		for (BloomFilter<T> bf:blocks)
			bf.put(obj);
	}

	public boolean mightContain(T object) {
		for (BloomFilter<T> bf:blocks)
			if (!bf.mightContain(object))
				return false;
		return true;
	}
	
	public long countTrueBits() {
		long val=0;
		for (BloomFilter bf : blocks)
			val+=bf.getBits().getBitCount();
		return val;
	}
	
	/*public void addAllInts(HashSet<Integer>allints, int repeat) {
		// first find all hashes
		final int hashesPerBlock=this.getHashes();
		final int blocks=this.getNumberOfBlocks();
		final int lengthPerBlock=(int)this.getLength();
		final int hashesToGet = hashesPerBlock*blocks;
		final IHasher hasher = new defaultHasher2(config.DEFAULT_HASHING, 0);
		final boolean[] cachedblocks[]= new boolean[blocks][lengthPerBlock];
		for (Integer i:allints) {
			// compute hash for all blocks
			final int[] ptr = hasher.getHashValuesInt(i + repeat, lengthPerBlock, hashesToGet);
			int cnt=0;
			for (int blockid=0;blockid<blocks;blockid++) {
				for (int hashid=0;hashid<hashesPerBlock;hashid++) {
					cachedblocks[blockid][ptr[cnt++]]=true;
				}
			}
		}
		// and now set myBitarrays accordingly
		for (int cnt=0;cnt<blocks;cnt++) {
			final boolean[] singleBlock = cachedblocks[cnt];
			final BloomFilter bf = this.blocks[cnt];
			for (int i=0;i<lengthPerBlock;i++)
				if (singleBlock[i])
					bf.myBitArray.set(i);
		}
	}
	
	public void addInt(int i) {
		for (BloomFilter bf:blocks)
			bf.addInt(i);
	}
	
	public void addString(String s) {
		for (BloomFilter bf:blocks)
			bf.addObj(s);
	}*/

	/*public ProbabilisticEstimation estimateRangeAndCardinality(double probRequired) {
		return estimateRangeAndCardinality(probRequired, this.getLength(), this.getHashes(), this.countTrueBits(), this.blocks.length);
	}

	public static ProbabilisticEstimation estimateRangeAndCardinality(double probRequired, long length, int k, long trueBits, int numberOfBlocks) {
		final double i = trueBits;
		final double n = estimateCardinality(trueBits, length, k, numberOfBlocks);
		double probExpected = 0;
		double nMax = n, nMin = n;
		double r = 1;

		if (i == 0)
			return new ProbabilisticEstimation(0, 0, 0, 1);

		// now find the bound with binary search
		while (probExpected < probRequired) {
			nMax = n + r;
			nMin = n - r;
			// find probability
			probExpected = BlockPartitionedBloomFilter.estimateProbabilityInCardinalityRange(nMin, nMax, trueBits, length, k, numberOfBlocks);
			r *= 2;
		}
		r/=2; // the last multiplication was unnecessary
		// and now i reduce
		double rMin =  (r / 2d);
		double rMax =  r;
		double nMaxOld=0,nMinOld=0;
		double prevProbExpected=0;
		while (rMin < rMax) {
			prevProbExpected=probExpected;
			r = rMin + ((rMax - rMin) / 2d); // Note: not (low + high) / 2 !!
			nMax = n + r;
			nMin = n - r;
			// find probability
			if (nMax==nMaxOld && nMin==nMinOld)
				break; // nothing changed
			nMaxOld=nMax; nMinOld=nMin;
			probExpected = estimateProbabilityInCardinalityRange(nMin, nMax, trueBits, length, k, numberOfBlocks);

			if (probExpected < probRequired)
				rMin = r + 1;
			else
				rMax = r;
		}
		// high == low, using high or low depends on taste
		r = rMin;
		ProbabilisticEstimation pest = new ProbabilisticEstimation(n, nMin-1, nMax+1, prevProbExpected);
		return pest;
	}
	
	public static double estimateProbabilityInCardinalityRange(double lown, double highn, long truebits, long length, int k, int numberOfBlocks ) {
		double imin = estimateNumberOfTrueBits(lown, length, k, numberOfBlocks);
		double imax = estimateNumberOfTrueBits(highn, length, k, numberOfBlocks);
		final double a, b;
		final double i = truebits;
		final double deltaleft = (i - 1d - imin) / imin;
		final double deltaright = (imax - i - 1d) / imax;
		if (deltaleft < 0)
			a = 0;
		else
			a = -Math.pow((i-1-imin), 2d) / (4d * imin); // simplified chernoff bound, especially suitable for large BFs
		//		Math.exp(i - 1 - imin) * Math.pow((imin / (i - 1d)), i - 1d);

		if (deltaright < 0)
			b = 0;
		else
			b = -Math.pow((imax - i - 1d), 2d) / (2d * imax); // deltaright= (imax-i-1)/imax

		if (deltaleft < 0 && deltaright < 0)
			return 0;

		double val = 1d - Math.exp(a) - Math.exp(b);
		if (Double.isNaN(val)) {
			System.err.println("ISNaN in estimateProbabilityInCardinalityRange");
			return 0;//estimateProbabilityInCardinalityRangeBD(lown, highn);
		}
		else
			return val;
	}
	// the same with corresponding function of bloom filter	
	public final double estimateProbabilityInCardinalityRange(double lown, double highn) {
		return BlockPartitionedBloomFilter.estimateProbabilityInCardinalityRange(lown, highn, 
				this.countTrueBits(), this.getLength(), this.getHashes(), this.getNumberOfBlocks());
	}
		
	public double estimateCardinality() {
		return estimateCardinality(this.countTrueBits(), this.getLength(), this.getHashes(), this.getNumberOfBlocks());
	}
	
	public double estimateNumberOfTrueBits(double n) {
		return estimateNumberOfTrueBits(n, this.getLength(), this.getHashes(), this.getNumberOfBlocks());
	}
	public static double estimateNumberOfTrueBits(double n, double Length, double Hashes, int numberOfBlocks) {
		return numberOfBlocks * BloomFilter.estimateNumberOfTrueBits(n, Length, Hashes);
	}*/
	
	public double estimateCardinality() {
		double trueBits = countTrueBits();
		
		double val = Math.log( 1d- trueBits/(double)(getNumberOfBlocks()*getLength()) );
		val = val / (getHashes()*Math.log(1d-1d/getLength()));
		return val;
	}

	/*
	// editing distance for query execution. Checks only the true bits
	public static long getEditingDistance(BlockPartitionedBloomFilter source, BlockPartitionedBloomFilter target) {
		long val = 0;
		for (int cnt=0;cnt<source.blocks.length;cnt++) {
			val+=BloomFilter.getEditingDistance(source.blocks[cnt], target.blocks[cnt]);
		}
		return val;
	}
	
	public static BlockPartitionedBloomFilter getUnion(BlockPartitionedBloomFilter source, BlockPartitionedBloomFilter target) {
		BlockPartitionedBloomFilter minLength, maxLength;
		if (source.blocks[0].getLength() != target.blocks[0].getLength())
			return null; // cannot compare different size filters
		else {
			if (source.blocks.length==target.blocks.length) {
				minLength = new BlockPartitionedBloomFilter(source.blocks.length, source);
				maxLength = target;
			}
			else {
				minLength = new BlockPartitionedBloomFilter(target.blocks.length, target);
				maxLength = source;
			}
		}
		for (int cnt=0;cnt<minLength.blocks.length;cnt++) {
			minLength.blocks[cnt].getBitArray().or(maxLength.blocks[cnt].getBitArray());
		}
		return minLength;
	}

	public static BlockPartitionedBloomFilterAND getIntersection(BlockPartitionedBloomFilter source, BlockPartitionedBloomFilter target) {
		BlockPartitionedBloomFilter minLength, maxLength;
		if (source.blocks[0].getLength() != target.blocks[0].getLength())
			return null; // cannot compare different size filters
		else {
			if (source.blocks.length==target.blocks.length) {
				minLength = new BlockPartitionedBloomFilter(source.blocks.length, source);
				maxLength = target;
			}
			else {
				minLength = new BlockPartitionedBloomFilter(target.blocks.length, target);
				maxLength = source;
			}
		}
		for (int cnt=0;cnt<minLength.blocks.length;cnt++) {
			minLength.blocks[cnt].getBitArray().and(maxLength.blocks[cnt].getBitArray());
		}
		BlockPartitionedBloomFilterAND bbfand = new BlockPartitionedBloomFilterAND(minLength, minLength, maxLength);
		return bbfand;
	}

	public static long getTrueBitsUnion(BlockPartitionedBloomFilter source, BlockPartitionedBloomFilter target) {
		try {
			return getUnion(source, target).countTrueBits();
		} catch (Exception isNull) {
			return -1;
		}
	}
	public static long getTrueBitsIntersection(BlockPartitionedBloomFilter source, BlockPartitionedBloomFilter target) {
		try {
			return getIntersection(source, target).countTrueBits();
		} catch (Exception isNull) {
			return -1;
		}
	}
	
	public double getErrorProbability() {
		double prob = 1;
		for (BloomFilter bf:blocks) {
			prob = prob*bf.getErrorProbability();
		}
		return prob;
	}

	public static double computeErrorProbability(double hashes, double length, double numberOfBlocks, double numberOfElements) {
		double probperBlock = BloomFilter.computeErrorProbability(hashes, length, numberOfElements);
		return Math.pow(probperBlock, numberOfBlocks);
	}*/

	/*public BlockPartitionedBloomFilter reduceBlocks(int numberOfBlocks) {
		BlockPartitionedBloomFilter bbf = new BlockPartitionedBloomFilter(numberOfBlocks, this);
		return bbf;
	}

	public BlockPartitionedBloomFilter reduceBlocks(double probability) {
		final double trueBitsTotal = this.countTrueBits();
		final double trueBitsPerFilter= trueBitsTotal/(double)this.getNumberOfBlocks();
		double fpProbPerFilter = Math.pow(trueBitsPerFilter/this.getLength(), this.getHashes());
		double requiredBlocks = Math.ceil(Math.log(probability)/Math.log(fpProbPerFilter));
		if (requiredBlocks>=this.getNumberOfBlocks())
			requiredBlocks=this.getNumberOfBlocks();		
		return new BlockPartitionedBloomFilter((int)requiredBlocks, this);
	}*/
}

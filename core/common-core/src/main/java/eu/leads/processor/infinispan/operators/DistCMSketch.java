package eu.leads.processor.infinispan.operators;

import org.infinispan.Cache;

import java.io.Serializable;
import java.util.Random;

abstract class sketchArray{
	abstract int getValue(int x, int y);
	abstract void putValue(int x, int y, int newValue);
	abstract void increase(int x, int y, int inc);

}

// NOT thread-safe, but we don't care!
class DistArray extends sketchArray {
	Cache<Integer,Integer> ArrayCache=null;

	int width=0;
	int depth=0;
	public DistArray(int w, int d, Cache<Integer,Integer> ArrayCache, boolean reload  ) {
		this.ArrayCache = ArrayCache;
		if(reload) {
			width =ArrayCache.get(-1);
			depth = ArrayCache.get(-2);
		}else{
			this.ArrayCache.put(-1, w);
			this.ArrayCache.put(-2, d);
		}
	}
	int getValue(int x, int y) {
		if(ArrayCache.containsKey(y*width+x))
			return ArrayCache.get(y * width + x);
		return -1;
	}
	void putValue(int x, int y, int newValue) {
		ArrayCache.put(y * width + x, newValue);
	}
	void increase(int x, int y, int inc) {
		putValue(x,y,getValue(x,y)+inc);
	}
}

// NOT thread-safe, but we don't care!
final class LocalArray extends sketchArray{
	public static int[][] getArray() {
		return Array;
	}

	public static int getWidth() {
		return width;
	}

	public static int getDepth() {
		return depth;
	}

	//	Cache<Integer,Integer> ArrayCache=null;
	static int[][] Array=null;
	static int width=0;
	static int depth=0;
	protected LocalArray(int w, int d) {
		Array = new int[w][d];
	if(width==0)
		for(int x=0;x<w;x++)
			for(int y=0;y<d;y++)
				Array[x][y]=-1;
		width=w;
		depth=d;
	}
	int getValue(int x, int y) {
		 return Array[x][y];
	}
	void putValue(int x, int y, int newValue) {
		Array[x][y]= newValue;
	}
	void increase(int x, int y, int inc) {
		Array[x][y]+=inc;
	}
}

public class DistCMSketch {
	final sketchArray darray; // initialize in the constructor!
	final Random rn = new Random();
	final int w, d; // w=mod, d=levels
	
	// LEFTERIS USE THIS constructor. No parameters required
	public DistCMSketch(Cache<Integer,Integer> ArrayCache, boolean load) {
		this(0.01,0.001,ArrayCache, load);
	}
	
	public DistCMSketch(double delta, double epsilon,Cache<Integer,Integer> ArrayCache, boolean load) {
		double epsilonEach=epsilon;

		w=(int)Math.ceil(Math.E/epsilonEach);
		d=(int)Math.ceil(Math.log(1d/delta));
		if(ArrayCache==null)
			darray=new LocalArray(w,d);
		else
			darray= new DistArray(w,d,ArrayCache, load);
		// initialize a and b
		Random mt = new Random(1234);
		alphas=new long[d];
		betas=new long[d];
		for (int i=0;i<d;i++) {
			alphas[i]=Math.abs(mt.nextInt());
			betas[i]=Math.abs(mt.nextInt());
		}
	}

	public void storeAsObject(Cache<String,Object> sketchCache, String prefix){
		if(darray instanceof LocalArray) {
			sketchCache.put(prefix + "w", ((LocalArray) darray).getWidth());
			sketchCache.put(prefix + "d", ((LocalArray) darray).getDepth());
			sketchCache.put(prefix + "array",  ((LocalArray) darray).getArray());
		}
	}


	public DistCMSketch(double delta, double epsilon, int[][]array, Cache<Integer,Integer> ArrayCache, boolean load) {
		double epsilonEach=epsilon;
		w=(int)Math.ceil(Math.E/epsilonEach);
		d=(int)Math.ceil(Math.log(1d/delta));
		darray=new LocalArray(w,d);//=new DistArray(w,d,ArrayCache, load);
		// initialize a and b
		Random mt = new Random(1234);
		alphas=new long[d];
		betas=new long[d];
		for (int i=0;i<d;i++) {
			alphas[i]=Math.abs(mt.nextInt());
			betas[i]=Math.abs(mt.nextInt());
		}
	}

	int [] prepareRandomValues(int number){
		int[] randomValues=new int[number];
		Random mt = new Random(1234);
		for (int cnt=0;cnt<number;cnt++)
			randomValues[cnt]=mt.nextInt();
		return randomValues;
	}

	long[] alphas;
	long[] betas;

	final int []hash(Object type, int levels, int mod) {
		return hashRandom(type.hashCode(), levels, mod);
	}
	final int []hashFn(Object type, int levels, int mod) {
		System.err.println("This function is repetitive on the mod, don't use it!");
		int [] hash = new int[levels];
		for (int i=0;i<levels;i++) hash[i]=(int)((alphas[i]*(type.hashCode())+betas[i])%mod);
		return hash;
	}
	synchronized int []hashRandom(Object type, int levels, int mod) {
		int [] hash = new int[levels];
		rn.setSeed(type.hashCode());
		for (int i=0;i<levels;i++) hash[i]=rn.nextInt(mod);
		return hash;
	}
	
	public void add(Object type) {
		int[]hashes = hash(type, d, w);
		for (int depth=0;depth<d;depth++) {
			int w = hashes[depth];
			darray.increase(w, depth, +1);
		}
	}
	
	public void add(Object type, int freq) {
		int[]hashes = hash(type, d, w);
		for (int depth=0;depth<d;depth++) {
			int w = hashes[depth];
			darray.increase(w, depth, freq);
		}
	}

	public double get(Object type) {
		int[]hashes = hash(type, d, w);
		double val = Double.MAX_VALUE;
		for (int depth=0;depth<d;depth++) {
			int w = hashes[depth];
			val = Math.min(val,darray.getValue(w, depth));
		}
		return val;
	}
}
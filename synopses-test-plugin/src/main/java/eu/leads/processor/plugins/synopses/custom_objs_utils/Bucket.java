package eu.leads.processor.plugins.synopses.custom_objs_utils;

class Bucket implements Cloneable {
	int time;
	float trueBits;
	public Bucket(int time, float trueBits) {
		this.time = time; this.trueBits = trueBits;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public void setTrueBits(float trueBits) {
		this.trueBits = trueBits;
	}
	public int getTime() {
		return time;
	}
	public float getTrueBits() {
		return trueBits;
	}
	public void addTrueBits(float tb) {
		this.trueBits+=tb;
	}
	public Bucket clone() {
		return new Bucket(this.time, this.trueBits);
	}
}


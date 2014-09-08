package eu.leads.processor.plugins.synopses.custom_objs_utils;

public class Pair {
	public final int time; 
	public final double estimation;
	public Pair(int time, double estimation) {
		this.time=time;
		this.estimation=estimation;
	}
	public String toString() {
		return "(T:" + time + " E:" + estimation + ")";
	}
}

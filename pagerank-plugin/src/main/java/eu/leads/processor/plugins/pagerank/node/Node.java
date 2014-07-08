package eu.leads.processor.plugins.pagerank.node;

import gnu.trove.set.hash.THashSet;

import java.io.Serializable;

public class Node implements Serializable {
	
	protected THashSet neighbours;
	
	public Node() {
		neighbours = new THashSet();
	}

	public THashSet getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(THashSet neighbours) {
		this.neighbours = neighbours;
	}
	
}

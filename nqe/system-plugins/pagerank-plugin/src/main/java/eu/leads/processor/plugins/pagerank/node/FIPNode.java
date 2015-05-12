package eu.leads.processor.plugins.pagerank.node;

import gnu.trove.map.hash.THashMap;
import java.util.TreeMap;

public class FIPNode extends Node{
    static final long serialVersionUID = -1828291873628101L;
    protected int fipVisits;
	protected THashMap<Object,TreeMap<Integer, Object>> fip_map;//<RWid, Object>, where Object: sortedMap with entries <curr_pos_at_RWid, nextNode>

	public FIPNode() {
		super();
		fip_map = new THashMap<Object,TreeMap<Integer, Object>>();

        fipVisits = 0;
	}
	public THashMap<Object,TreeMap<Integer, Object>> getFip_map() {
		return fip_map;
	}

	public void setFip_map(THashMap<Object,TreeMap<Integer, Object>> fip_map) {
		this.fip_map = fip_map;
	}

    public int getFipVisits(){
        return fipVisits;
    }

    public void setFipVisits(int fipVisits){
        this.fipVisits = fipVisits;
    }

    public void fipVisitsPlus1(){
        fipVisits++;
    }

    public void fipVisitsMinus1(){
        fipVisits--;
    }
}

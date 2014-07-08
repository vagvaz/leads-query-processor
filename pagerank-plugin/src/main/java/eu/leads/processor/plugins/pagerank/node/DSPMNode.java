package eu.leads.processor.plugins.pagerank.node;

import gnu.trove.map.hash.TObjectIntHashMap;

public class DSPMNode extends FIPNode {

    protected int dspmVisits;

    protected TObjectIntHashMap stepChoices;
	protected int pend;

	public DSPMNode(Object id) {
		super();
		stepChoices = new TObjectIntHashMap();

        dspmVisits = 0;
    }

    public int getDspmVisits() {
        return dspmVisits;
    }

    public void setDspmVisits(int dspmVisits) {
        this.dspmVisits = dspmVisits;
    }

	public int getPend() {
		return pend;
	}

	public void setPend(int pend) {
		this.pend = pend;
	}
	
	public TObjectIntHashMap getStepChoices() {
		return stepChoices;
	}

	public void setStepChoices(TObjectIntHashMap stepChoices) {
		this.stepChoices = stepChoices;
	}

    public int getVisitCount(){
        return dspmVisits + fipVisits;
    }
	
}

package eu.leads.distsum.Utils;

import java.util.HashMap;
import java.util.Set;

public class ViolationObject {

    private HashMap<Object, Double> partialDataValues;
    private double borderValue;
    Set<Object> violatedTopk, violatedRest;
    boolean emptyTopk;
    //long epoch;

    public ViolationObject(HashMap<Object, Double> partialDataValues, Set<Object> violatedTopk, Set<Object> violatedRest, double borderValue, boolean emptyTopk) {
        this.borderValue = borderValue;
        this.partialDataValues = partialDataValues;
        this.violatedTopk = violatedTopk;
        this.violatedRest = violatedRest;
        this.emptyTopk = emptyTopk;
        // this.epoch = epoch;
    }

    public HashMap<Object, Double> getPartialDataValues() {
        return partialDataValues;
    }

    public void setPartialDataValues(HashMap<Object, Double> partialDataValues) {
        this.partialDataValues = partialDataValues;
    }

    public double getBorderValue() {
        return borderValue;
    }

    public void setBorderValue(double borderValue) {
        this.borderValue = borderValue;
    }

    public Set<Object> getViolatedTopk() {
        return violatedTopk;
    }

    public void setViolatedTopk(Set<Object> violatedTopk) {
        this.violatedTopk = violatedTopk;
    }

    public Set<Object> getViolatedRest() {
        return violatedRest;
    }

    public void setViolatedRest(Set<Object> violatedRest) {
        this.violatedRest = violatedRest;
    }

    public boolean isEmptyTopk() {
        return emptyTopk;
    }

    public void setEmptyTopk(boolean emptyTopk) {
        this.emptyTopk = emptyTopk;
    }

    /*public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }*/
}

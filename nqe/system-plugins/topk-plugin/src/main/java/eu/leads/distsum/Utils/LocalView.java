package eu.leads.distsum.Utils;

import java.util.HashMap;

/**
 * Created by dell on 8/25/14.
 */
public class LocalView {

    HashMap<Object, Double> partialValues;
    double border;

    public LocalView(HashMap<Object, Double> partialValues, double border) {
        this.border = border;
        this.partialValues = partialValues;
    }

    public HashMap<Object, Double> getPartialValues() {
        return partialValues;
    }

    public void setPartialValues(HashMap<Object, Double> partialValues) {
        this.partialValues = partialValues;
    }

    public double getBorder() {
        return border;
    }

    public void setBorder(double border) {
        this.border = border;
    }
}

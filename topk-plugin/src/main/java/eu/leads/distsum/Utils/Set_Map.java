package eu.leads.distsum.Utils;

import java.util.Map;
import java.util.Set;

/**
 * Created by dell on 8/26/14.
 */
public class Set_Map {

    private Set<Object> topkset;
    private Map<Object, Double> deltas;

    public Set_Map(Map<Object, Double> deltas, Set<Object> topkset) {
        this.deltas = deltas;
        this.topkset = topkset;
    }

    public Set<Object> getTopkset() {
        return topkset;
    }

    public void setTopkset(Set<Object> topkset) {
        this.topkset = topkset;
    }

    public Map<Object, Double> getDeltas() {
        return deltas;
    }

    public void setDeltas(Map<Object, Double> deltas) {
        this.deltas = deltas;
    }
}

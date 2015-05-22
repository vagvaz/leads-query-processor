package eu.leads.processor.infinispan;

import org.infinispan.filter.KeyValueFilter;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.metadata.Metadata;

/**
 * Created by vagvaz on 22/05/15.
 */
public class LocalDataFilter<K,V> implements KeyValueFilter<K, V> {
    ClusteringDependentLogic cdl;
    public LocalDataFilter(ClusteringDependentLogic cdl) {
        this.cdl = cdl;
    }

    @Override public boolean accept(K key, V value, Metadata metadata) {
        if(cdl.localNodeIsPrimaryOwner(key))
            return true;
        return false;
    }
}

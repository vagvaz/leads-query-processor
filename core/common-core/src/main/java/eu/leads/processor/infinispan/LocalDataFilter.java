package eu.leads.processor.infinispan;

import eu.leads.processor.common.utils.ProfileEvent;
import org.infinispan.filter.KeyValueFilter;
import org.infinispan.filter.KeyValueFilterConverter;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by vagvaz on 22/05/15.
 */
public class LocalDataFilter<K,V> implements KeyValueFilterConverter<K, V,V>,Serializable {
//    ClusteringDependentLogic cdl;
//    Logger profilerLog;
//    ProfileEvent event;
    public LocalDataFilter(ClusteringDependentLogic cdl) {
//        this.cdl = cdl;
//        profilerLog  = LoggerFactory.getLogger("###PROF###" + this.getClass().toString());
//        event = new ProfileEvent("dataFilter",profilerLog);
    }

    @Override public boolean accept(K key, V value, Metadata metadata) {
        boolean result = true;
//        event.start("dataFilter " + key.toString());
//        if(cdl.localNodeIsPrimaryOwner(key))
//            result=true;
//        event.end();
        return result;

    }

    @Override
    public V filterAndConvert(K key, V value, Metadata metadata) {
//        boolean result = false;
        //        event.start("dataFilter " + key.toString());
//        if(cdl.localNodeIsPrimaryOwner(key))
//            result=true;
        //        event.end();
//        if(result){
            return value;
//        }
//        else
//            return null;
    }

//    @Override
    public V convert(K key, V value, Metadata metadata) {
        return value;
    }
}

package eu.leads.infext.proc.batch.mapreduce;

import java.util.Iterator;

import org.infinispan.distexec.mapreduce.Reducer;

import eu.leads.processor.infinispan.LeadsReducer;

public class DefaultReducer extends LeadsReducer<Object, Object> {
	
	private static final long serialVersionUID = 835066292446759205L;

	@Override
	public Object reduce(Object arg0, Iterator<Object> iterator) {
        Object firstValue = iterator.next();
        return firstValue;
	}

}

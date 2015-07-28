package eu.leads.infext.proc.batch.mapreduce;

import org.infinispan.distexec.mapreduce.Reducer;

import java.util.Iterator;

public class DefaultReducer implements Reducer<Object, Object> {

	@Override
	public Object reduce(Object arg0, Iterator<Object> iterator) {
        Object firstValue = iterator.next();
        return firstValue;
	}

}

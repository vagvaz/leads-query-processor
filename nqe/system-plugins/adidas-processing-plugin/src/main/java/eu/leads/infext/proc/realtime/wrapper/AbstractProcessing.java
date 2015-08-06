package eu.leads.infext.proc.realtime.wrapper;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.realtime.hook.AbstractHook;

import java.util.HashMap;

public abstract class AbstractProcessing {
	
	protected AbstractHook hook = null;

	public AbstractProcessing(AbstractHook hook) {
		this.hook = hook;
	}
	
	public abstract void process(String url, String timestamp, HashMap<String, HashMap<String,String>> metadata, HashMap<String, MDFamily> editableFamilies);
	
}

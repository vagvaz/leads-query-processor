package eu.leads.infext.proc.realtime.wrapper;

import java.util.HashMap;
import java.util.List;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.realtime.hook.AbstractHook;

public abstract class AbstractProcessing {
	
	protected AbstractHook hook = null;

	public AbstractProcessing(AbstractHook hook) {
		this.hook = hook;
	}
	
	public abstract void process(String url, String timestamp, HashMap<String, HashMap<String,Object>> metadata, HashMap<String, MDFamily> editableFamilies);
	
	public String getHookName() {
		return hook.getClass().getName();
	}
	
}

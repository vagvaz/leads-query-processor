package eu.leads;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ProcessingFilterSingleton {
	
	private static String [] filter = null;
	private static String filterStr = null;

	public static void setFilterString(String str) {
		filterStr = str;
	}
	
	public static void setFilter(String [] filter) {
		ProcessingFilterSingleton.filter = filter;
	}

	public static boolean shouldProcess(String uri) {
		if(filter!=null && StringUtils.startsWithAny(uri, filter))
			return true;
		else if(filterStr!=null && uri.contains(filterStr))
			return true;
		else if(filter==null && filterStr==null)
			return true;
		return false;
	}

}

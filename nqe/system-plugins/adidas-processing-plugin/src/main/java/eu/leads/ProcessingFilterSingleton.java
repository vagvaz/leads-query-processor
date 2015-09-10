package eu.leads;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ProcessingFilterSingleton {
	
	private static String [] filter = null;

	public static void setFilterString(String str) {
		filter = new String[] {str};
	}
	
	public static void setFilter(String [] filter) {
		ProcessingFilterSingleton.filter = filter;
	}

	public static boolean shouldProcess(String uri) {
		if(filter==null)
			return true;
		for(String f : filter){
			if(uri.startsWith(f)){
				return true;
			}
		}

		return false;
	}

}

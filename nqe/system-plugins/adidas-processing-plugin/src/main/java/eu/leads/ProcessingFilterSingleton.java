package eu.leads;

public class ProcessingFilterSingleton {
	
	private static String filterString = null;

	public static void setFilterString(String str) {
		ProcessingFilterSingleton.filterString = str;
	}

	public static boolean shouldProcess(String uri) {
		if(filterString==null)
			return true;
		if(uri.contains(filterString))
			return true;
		
		return false;
	}

}

package eu.leads.datastore.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import static java.lang.Thread.sleep;

public class LeadsQueryInterface {
	
	private static boolean isInitialized = false;
	private static boolean queryMode = true;
	
	public static void setQueryMode(boolean on) {
		queryMode = on;
	}
	
	public static boolean initialize(String url, int p) {
		try {
			if(!isInitialized) {
				System.out.println("Initializing WebServiceClient with "+url+":"+p);
				if(!WebServiceClient.initialize(url, p))
					System.exit(-1);
				isInitialized = true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    public static QueryResults execute(String sql) {
    	QueryResults res = null;
		System.out.println(sql);
		
		String lowerSql = sql.toLowerCase().trim();
		boolean isInsert = false;
		
		if(lowerSql.startsWith("insert"))
			isInsert = true;
		
		if(queryMode) {
	    	/* TIME */ long start = System.currentTimeMillis();
	    	
	    	if(isInsert) {
	    		try {
	    			QueryStatus currentStatus = WebServiceClient.submitQuery("adidas",sql);
		    		System.out.println("The query with id " + currentStatus.getId() + " is submitted.");
	    		}
	    		catch (IOException e) {
		    		e.printStackTrace();
	    		}
	    	}
	    	else {
		    	for(int i=0; i<3; i++) {
			    	try {
				        QueryStatus currentStatus = WebServiceClient.submitQuery("adidas",sql);
				        String statusString = "";
				        while(!currentStatus.getStatus().equals("COMPLETED") && !currentStatus.getStatus().equals("FAILED")) {
				            sleep(100);
				            currentStatus = WebServiceClient.getQueryStatus(currentStatus.getId());
				            String currentStatusString = currentStatus.getStatus();
				            if(!statusString.equals(currentStatusString))
				            	System.out.println("The query with id " + currentStatus.getId() + " is " + currentStatus.getStatus());
				            statusString = currentStatusString;
				        }
				        System.out.println("The query with id " + currentStatus.getId() + " " + currentStatus.getStatus());
				        if(currentStatus.getStatus().equals("COMPLETED")) {
				            System.out.println("Wait while we fetching your result...");
				            res = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
				        }
				        else{
				            System.out.println("because " + currentStatus.getErrorMessage());
				        }
			        
			    	} catch (IOException e) {
			    		e.printStackTrace();
			    		sleep(500);
			    		System.err.println("Repeating...");
			    		continue;
			    	}
			    	break;
		    	}
	    	}
	    	
	    	/* TIME */ System.err.println("+++ LeadsQueryInterface.sendQuery() time for '"
	    					+ (sql.length()>40 ? sql.substring(0,40) : sql) + "...':"
	    					+ ((System.currentTimeMillis()-start)/1000.0)+" s");
		}
    	
        return res;

    }
	
}

package eu.leads;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ProcessingFilterSingletonTest extends TestCase {
	
	private String filterString = "com.runnersworld;com.runblogger;com.solereview;uk.co.bbc;com.footwearnews;com.runningshoesguru;com.competitor";

	public void testShouldProcess() {
		String[] filter = filterString.split(";");
		ProcessingFilterSingleton.setFilter(filter);
		
		boolean isProcessed = ProcessingFilterSingleton.shouldProcess("com.runnersworld.www:http/3rwexweq");
		Assert.assertTrue(isProcessed);
		isProcessed = ProcessingFilterSingleton.shouldProcess("uk.co.bbc.www:http/0/sport/");
		Assert.assertTrue(isProcessed);
		isProcessed = ProcessingFilterSingleton.shouldProcess("com.holabirdsports.www:http/shoe/xpg.html");
		Assert.assertFalse(isProcessed);
	}

}

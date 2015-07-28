package test.eu.leads.infext.proc.batch.exec;

import eu.leads.infext.proc.batch.exec.SiteDefinerExecutor;
import test.EnvironmentInit;

public class SiteDefinerExecutorTest {

	public static void main(String[] args) throws Exception {
		EnvironmentInit.initiateEnv();
		SiteDefinerExecutor.main(new String [] {"uk.co.zalando.www:http/"});
	}
	
}

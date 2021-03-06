package eu.leads.infext.logging.redirect;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Formatter;

public class StdLoggerRedirect {
	
	private static boolean append = false;

	private static PrintStream stdout;                                        
	private static PrintStream stderr;
	
	private static boolean isExecuted = false;
	
	private static Handler fileHandler = null;
	
	public static void initLogging(String dir) throws Exception {
		
		if(!isExecuted) {
			System.err.println("Turning logging on, check logging files");
			
			// initialize logging to go to rolling log file
			LogManager logManager = LogManager.getLogManager();
			logManager.reset();
			
			final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		
			// log file max size 10K, 3 rolling files, append-on-open
			Formatter formatter = new Formatter() {
			      public String format(LogRecord record) {
			          return record.getLevel() + "\t" + dateFormat.format(new Date(record.getMillis())) + ":\t" + 
			        		  /*record.getSourceClassName() + "::" + record.getSourceMethodName() + ":\t" +*/
			        		 record.getMessage() + "\n";
			        }
			};			
			
			File file = new File(dir);
			file.mkdirs();
			
			fileHandler = new FileHandler(dir+"/leads-lucene-out", 10000000, 100, append);
			fileHandler.setFormatter(formatter);
			Logger.getLogger("stdout").addHandler(fileHandler);
			
			fileHandler = new FileHandler(dir+"/leads-lucene-err", 10000000, 100, append);
			fileHandler.setFormatter(formatter);
			Logger.getLogger("stderr").addHandler(fileHandler);
			
	        // preserve old stdout/stderr streams in case they might be useful      
	        stdout = System.out;                                        
	        stderr = System.err;                                        
	
	        // now rebind stdout/stderr to logger                                   
	        Logger logger;                                                          
	        LoggingOutputStream los;                                                
	
	        logger = Logger.getLogger("stdout");                                    
	        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);           
	        System.setOut(new PrintStream(los, true));                              
	
	        logger = Logger.getLogger("stderr");                                    
	        los= new LoggingOutputStream(logger, StdOutErrLevel.STDERR);            
	        System.setErr(new PrintStream(los, true)); 

			isExecuted = true;
		}
        
    } 
	
	public static void closeLogging() {
		if(fileHandler != null) {
			fileHandler.close();                                  
	        System.setOut(stdout);                       
	        System.setErr(stderr); 
			stderr.println("Closing program...");
		}
		
	}
	
}

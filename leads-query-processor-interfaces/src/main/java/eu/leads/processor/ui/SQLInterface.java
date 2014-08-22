package eu.leads.processor.ui;

import eu.leads.processor.common.Tuple;
import eu.leads.processor.conf.LQPConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vagvaz on 8/22/14.
 */
public class SQLInterface {
   private static final Logger log = LoggerFactory.getLogger(SQLInterface.class.getCanonicalName());
   private static String user;
   private static Set<String> uris;
   private static final  Object mutex = new Object();
   private static Map<String,String> pendingQueries;
   private static Map<String,List<Tuple>> resultSets;
   private static String confFileName;
   private static InputHandler inputHandler;
   private static OutputHandler outputHandler;
   private static WebServiceHandler webServiceHandler;


   public static void main(String[] args) {
      if(checkArguments(args)){
         confFileName = args[0];
         LQPConfiguration.initialize(true);
         LQPConfiguration.getInstance().loadFile(confFileName);
         readConfiguration();
         initializeStrucutres();
         inputHandler.start();
         outputHandler.start();
         webServiceHandler.start();
         try {
            inputHandler.join();
            outputHandler.join();
            webServiceHandler.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         System.out.println("Thank you for using the Command SQL Interface of leads query processor");
      }


   }

   private static void initializeStrucutres() {
      inputHandler = new InputHandler();
      outputHandler = new OutputHandler();
      webServiceHandler = new WebServiceHandler(uris);
   }

   private void shutdown(){
      synchronized (mutex) {
         inputHandler.quit();
         outputHandler.quit();
         webServiceHandler.quit();
      }
   }
   public static void processQuery(String sql){
      synchronized (mutex) {
         String queryId = webServiceHandler.sendQuery(sql);
         pendingQueries.put(queryId, sql);
      }
   }

   public static void resultsArrived(String queryId,List<Tuple> resultSet){
     synchronized (mutex) {
        pendingQueries.remove(queryId);
        outputHandler.printResults(resultSet);
     }
   }
   private static void readConfiguration() {

   }

   private static boolean checkArguments(String[] args) {
      boolean result = false;
      return result;
   }
}

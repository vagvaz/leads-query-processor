package data;

import au.com.bytecode.opencsv.CSVReader;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by vagvaz on 10/29/14.
 */
public class LoadCsv {

   static RemoteCacheManager manager=null;
   static InfinispanManager imanager=null;

   static ConcurrentMap embeddedCache=null;
   static RemoteCache remoteCache=null;
   public static void main(String[] args) throws IOException, ClassNotFoundException {
      if(args.length != 2  && args.length != 4)
      {
         System.err.println("Wrong number of arguments");
         System.err.println("program [load] directory host port (only in load)");
         System.exit(-1);
      }
      LQPConfiguration.initialize();
      if(args.length == 2)
         imanager = InfinispanClusterSingleton.getInstance().getManager();
      if(args[0].startsWith("l")){
        loadData(args);
      }
//      else{
//         storeData(args);
//      }
   }

   private static void loadData(String[] args) throws IOException, ClassNotFoundException {
      if(args.length != 2 && args.length != 4 ){
         System.err.println("wrong number of arguments for store $prog load dir/ $prog load dir host port");
         System.exit(-1);
      }

     if(args.length > 2 )
           manager = createRemoteCacheManager(args[2],args[3]);
       Long startTime = System.currentTimeMillis();
       Path dir = Paths.get(args[1]);
       List<File> files = new ArrayList<>();
       try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{csv}")) {
           for (Path entry: stream) {
               files.add(entry.toFile());
           }
       } catch (IOException x) {
           throw new RuntimeException(String.format("error reading folder %s: %s",
                   dir,
                   x.getMessage()),
                   x);
       }
       for(File csvfile: files){
           System.out.print("Loading file: " + csvfile.getName());
           Long filestartTime = System.currentTimeMillis();
           loadDataFromFile(csvfile);
           System.out.println("Loading time: " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - filestartTime, "HH:mm:ss,SSS"));
       }
//      System.out.println("Loading entities remotely");
//      if(args.length > 2 ){
//         loadDataWithRemote(args);
//      }else{
//         loadDataEmbedded(args);
//      }
       System.out.println("Loading finished.");
       System.out.println("Overall Folder Loading time: " +  DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss,SSS"));
       System.exit(0);
   }
   private static void loadDataFromFile(File csvfile){
       String filename[] = csvfile.getAbsolutePath().split(".csv");
       //System.out.println("Filename" + csvfile.getAbsolutePath()+" "+filename[0]);

       String fulltableName[] =  (csvfile.getName().split(".csv")[0]).split("-");
       String tableName = fulltableName[fulltableName.length-1];
       String keysFilename = filename[0]+".keys";
       Path path = Paths.get(keysFilename);

       BufferedReader keyReader=null;
       if (Files.exists(path)) {
           try {
               keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(keysFilename)));
           } catch (FileNotFoundException e) {
               System.out.println("Unable to read keys file, skipping " + filename[0]+".csv");
               e.printStackTrace();
               return;
           }
           System.out.println(" Loading key from file " + filename[0]+".keys");
       }else{
           System.err.println(" No keys file, skipping " + filename[0]+".csv");
           return;
       }
       //Read the keys

       ArrayList<Class> columnType = new ArrayList<>();
       ArrayList<String> columns = new ArrayList<>();
       //HashSet<String > primaryKeys = new HashSet<String>();
       String [] primaryKeys=null;
       int [] primaryKeysPos = null;
       try {
           String keyLine = "";

           while ((keyLine =keyReader.readLine()) != null) {
               if (keyLine.startsWith("#col")) {
                   keyLine = keyReader.readLine();//Next line got keys
                   if(keyLine==null){
                       System.err.print("No Column Key Data line after #collumnline");
                       return;
                   }
                   String [] keysTypePairs  = keyLine.split(",");
                   {
                        System.out.print("Must find #" + keysTypePairs.length + " column names, ");
                        for (String keyTypePair: keysTypePairs){
                            String [] pair = keyTypePair.trim().split("\\s+");
                            if(pair.length!=2){
                                System.err.print("Column Key Data are not correct! Key line must be at ,Column name space ColumnType, form");
                                continue;
                            }else{
                              columns.add(pair[0]);
                              if(pair[1].toLowerCase().equals("text"))
                                  columnType.add(String.class);
                              else if(pair[1].toLowerCase().equals("bigint"))
                                  columnType.add(Long.class);
                              else if(pair[1].toLowerCase().equals("int"))
                                  columnType.add(Long.class);
                              else if(pair[1].toLowerCase().equals("float"))
                                  columnType.add(Long.class);
                              else
                              {
                                  System.err.print("Column Key not recognized type: " + pair[1]);
                                  continue;
                              }
                            }
                        }
                       System.out.println("Recognized Columns #" + keysTypePairs.length);
                   }
               }else if (keyLine.toLowerCase().startsWith("#primary")){//Read the primary keys
                   keyLine = keyReader.readLine();//Next line got primary keys
                   if(keyLine==null){
                       System.err.print("No primary Key Data line after #primary");
                       return;
                   }
                   primaryKeys  = keyLine.trim().split(",");
                   for(int i=0;i<primaryKeys.length;i++)
                       primaryKeys[i]=primaryKeys[i].trim();

               }
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       if(primaryKeys==null){
           System.err.println("Unable to find primary keys not importing file !");
           return;
       }
       if(columnType.isEmpty()){
           System.err.println("Unable to find column keys not importing file !");
           return;
       }
       int pos = 0;
       primaryKeysPos= new int[primaryKeys.length];

       for(int i=0;i<primaryKeys.length;i++){
           if(columns.contains(primaryKeys[i])){
               primaryKeysPos[i]=columns.indexOf(primaryKeys[i]);
           }else{
               System.err.println("Oups primary key not among columns, stop importing");
               return;
           }
       }

       if(initialize_cache(tableName))
       try {

           CSVReader reader = new CSVReader(new FileReader(csvfile));
           String valueLine = "";
           int numofEntries=0;
           String [] StringData;
           System.out.println("Importing data ... ");
          // cache.startBatch();
           while (( StringData = reader.readNext()) != null){

               if (StringData.length != columns.size()) {
                   System.err.println("Columns size, data column size mismatch, stop importing");
                   return;
               }
               JsonObject data = new JsonObject();
               String key = StringData[primaryKeysPos[0]];
               for (int i = 1; i < primaryKeysPos.length; i++) {
                   key += ":" + StringData[primaryKeysPos[i]];
               }

               for (pos = 0; pos < StringData.length; pos++) {
                   if (columnType.get(pos) == String.class)
                       if (columns.get(pos).equals("textcontent") || tableName == "page_core")
                           data.putString(columns.get(pos), "");
                       else
                           data.putString(columns.get(pos), StringData[pos]);
                   else if (columnType.get(pos) == Long.class)
                       data.putNumber(columns.get(pos), Long.parseLong(StringData[pos]));
                   else if (columnType.get(pos) == Integer.class)
                       data.putNumber(columns.get(pos), Integer.parseInt(StringData[pos]));
                   else if (columnType.get(pos) == Float.class)
                       data.putNumber(columns.get(pos), Float.parseFloat(StringData[pos]));
                   else {
                       System.err.println("Not recognised type, stop importing");
                       return;
                   }
               }
               put(key,data.toString());

               numofEntries++;
               if(numofEntries%1000==0){
                   System.out.println("Imported: "+numofEntries);
                   //cache.endBatch(true);
                   //if(numofEntries%5000==0)
                       return;
               }
           }
           System.out.println("Totally Imported: "+numofEntries);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }


   }
   private static void put(String key, String value){

       if(remoteCache!=null)
           remoteCache.put(key, value);
       else if(embeddedCache!=null)
           embeddedCache.put(key, value);

   }

   private static boolean initialize_cache(String tableName){

       System.out.println(" Tablename: "+ tableName+ " Trying to create cache: "+ StringConstants.DEFAULT_DATABASE_NAME+"."+ tableName  );
       if(manager!=null)
           try{
               remoteCache =  manager.getCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName);
           }catch ( Exception e){
               System.err.println("Error " + e.getMessage() + " Terminating file loading.");
               return false;
           }
       else if(imanager!=null)
           embeddedCache =  imanager.getPersisentCache(StringConstants.DEFAULT_DATABASE_NAME +"."+ tableName);
       else{
           System.err.println("Not recognised type, stop importing");
           return false;
       }
       if(embeddedCache==null && remoteCache==null ){
           System.err.print("Unable to Crete Cache, exiting");
           System.exit(0);
       }
       return true;
   }


   private static RemoteCacheManager createRemoteCacheManager(String host, String port) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(host).port(Integer.parseInt(port));
      return new RemoteCacheManager(builder.build());
   }
}

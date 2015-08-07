package data;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by vagvaz on 05/13/15.
 */
public class LoadAmplab2 {
    private static long  AllstartTime;

    enum plugs {SENTIMENT, PAGERANK};
    transient protected static Random r;

    static HashMap<String,Integer> loaded_tuples;
    static HashMap<String,Long> loading_times;

    static int delay = 0;
    static RemoteCacheManager remoteCacheManager = null;
    static InfinispanManager imanager = null;
    static EnsembleCacheManager emanager;

    static ConcurrentMap embeddedCache = null;
    static RemoteCache remoteCache = null;
    static EnsembleCache ensembleCache = null;
    static ArrayList<EnsembleCache>  ecaches = new ArrayList<>();
    static boolean ensemple_multi = false;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        r = new Random(0);
        loaded_tuples = new HashMap<>();
        if (args.length == 0) {
            System.out.print(" Syntax:\tconvertadd filename {inputcollumn conversion}+ \n where convertion type: sentiment, pagerank");
            System.err.println("or  \t\t$prog loadIspn dir (delay per put)\n ");
            System.err.println("or  \t\t$prog loadRemote dir host port (delay per put)\n ");
            System.err.println("or  \t\t$prog loadEnsemble dir host:port(|host:port)+ (delay per put)\n ");
            System.err.println("or  \t\t$prog loadEnsembleMulti dir host:port(|host:port)+ (delay per put)\n ");

            System.exit(-1);
        }
        loaded_tuples = new HashMap<>();
        loading_times = new HashMap<>();

        LQPConfiguration.initialize();
        EnsembleCacheUtils.initialize();

        if (args[0].startsWith("l")) {
            if(args[0].equals("loadIspn")) {
                imanager = InfinispanClusterSingleton.getInstance().getManager();

            }else if(args[0].equals("loadRemote")) {
                if (args.length != 2 && args.length < 4) {
                    System.err.println("wrong number of arguments for load $prog load dir/ $prog load dir host port (delay per put)");
                    System.exit(-1);
                }
                remoteCacheManager = createRemoteCacheManager(args[2], args[3]);
            }  else if(args[0].startsWith("loadEnsemble")){
                if ( args.length < 3) {
                    System.err.println("or  \t\t$prog loadEnsemble(Multi) dir host:port(|host:port)+ (delay per put)\n ");
                    System.exit(-1);
                }

                if(args[0].equals("loadEnsembleMulti"))
                    ensemple_multi=true;
                if (args.length == 4) {
                    delay = Integer.parseInt(args[3]);
                    System.out.println("Forced delay per put : " + delay + " ms");
                }
                String ensembleString = args[2];
                System.out.println("Using ensemble string " + ensembleString);
                emanager = new EnsembleCacheManager((ensembleString));
                System.out.println("Emanager has " + emanager.sites().size() + " sites");
                emanager.start();
            }else{
                System.exit(0);
            }
            loadData(args[1],args[5], args[6]);
        }
    }



    private static void loadData(String path, String arg5, String arg6) throws IOException, ClassNotFoundException {
        Long startTime = System.currentTimeMillis();
        AllstartTime = System.currentTimeMillis();
        Path dir = Paths.get(path);
        List<File> files = new ArrayList<>();

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return (Files.isDirectory(file));
            }
        };

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
                filter)) {
            for (Path path1 : stream) {
                // Iterate over the paths in the directory and print filenames
                //System.out.println(path1.getFileName());

                dir = Paths.get(path+"/"+path1.getFileName());

                try (DirectoryStream<Path> stream1 = Files.newDirectoryStream(dir, "{part}*")) {
                    files.clear();
                    for (Path entry : stream1) {
                        files.add(entry.toFile());
                    }
                } catch (IOException x) {
                    throw new RuntimeException(String.format("error reading folder %s: %s", dir, x.getMessage()), x);
                }
                for (File csvfile : files) {
                    System.out.print("Loading file: " + csvfile.getName());
                    Long filestartTime = System.currentTimeMillis();
                    loadDataFromFile(csvfile,arg5,arg6);
                    System.out.println("Loading time: " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - filestartTime, "HH:mm:ss,SSS"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Loading finished.");
        System.out.println("Overall Folder Loading time: " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss,SSS"));
        for(String tableName : loaded_tuples.keySet()){
            System.out.println(" TableName: " + tableName + " # Tuples: " + loaded_tuples.get(tableName) +" Time "+ DurationFormatUtils.formatDuration(loading_times.get(tableName), "HH:mm:ss,SSS")  +" Mean Rate: " + loaded_tuples.get(tableName)/((float)loading_times.get(tableName)/1000.0));
        }
        System.exit(0);

    }

    private static void loadDataFromFile(File csvfile, String arg5, String arg6) throws IOException {
        String tableName = csvfile.getParentFile().getName();
        String keysFilename = csvfile.getAbsoluteFile().getParent() + "/" + tableName + ".keys";
        Path path = Paths.get(keysFilename);

        int maxTuples = Integer.parseInt(arg5);
        if(loaded_tuples.containsKey(tableName)){
            if(maxTuples > 0 && loaded_tuples.get(tableName)>maxTuples){
                System.out.println(" Max entries reached for " + tableName + " skiping " + csvfile.getName());
                return;
            }
        }else{
           loaded_tuples.put(tableName,0);
           loading_times.put(tableName,0L);
        }
        BufferedReader keyReader = null;
        if (Files.exists(path)) {
            try {
                keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(keysFilename)));
            } catch (FileNotFoundException e) {
                System.out.println("Unable to read keys file, skipping "+ tableName);
                e.printStackTrace();
                return;
            }
            System.out.println(" Loading key from file " + tableName + ".keys");
        } else {
            System.err.println(" No keys file, skipping " + tableName);
            return;
        }

        //Read the keys
        ArrayList<Class> columnType = new ArrayList<>();
        ArrayList<String> columns = new ArrayList<>();
        String[] primaryKeys = null;
        int[] primaryKeysPos = null;
        try {
            String keyLine = "";

            while ((keyLine = keyReader.readLine()) != null) {
                if (keyLine.startsWith("#col")) {
                    keyLine = keyReader.readLine();//Next line got keys
                    if (keyLine == null) {
                        System.err.print("No Column Key Data line after #collumnline");
                        return;
                    }
                    String[] keysTypePairs = keyLine.split(",");
                    {
                        //System.out.print("Must find #" + keysTypePairs.length + " column names, ");
                        for (String keyTypePair : keysTypePairs) {
                            String[] pair = keyTypePair.trim().split("\\s+");
                            if (pair.length != 2) {
                                System.err.print("Column Key Data are not correct! Key line must be at ,Column name space ColumnType, form");
                                continue;
                            } else {
                                columns.add(pair[0]);
                                if (pair[1].toLowerCase().equals("text"))
                                    columnType.add(String.class);
                                else if (pair[1].toLowerCase().equals("bigint"))
                                    columnType.add(Long.class);
                                else if (pair[1].toLowerCase().equals("int"))
                                    columnType.add(Long.class);
                                else if (pair[1].toLowerCase().equals("float"))
                                    columnType.add(Float.class);
                                else {
                                    System.err.print("Column Key not recognized type: " + pair[1]);
                                    continue;
                                }
                            }
                        }
                        //System.out.println("Recognized Columns #" + keysTypePairs.length);
                    }
                } else if (keyLine.toLowerCase().startsWith("#primary")) {//Read the primary keys
                    keyLine = keyReader.readLine();//Next line got primary keys
                    if (keyLine == null) {
                        System.err.print("No primary Key Data line after #primary");
                        return;
                    }
                    primaryKeys = keyLine.trim().split(",");
                    for (int i = 0; i < primaryKeys.length; i++)
                        primaryKeys[i] = primaryKeys[i].trim();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyReader.close();
        if (primaryKeys == null) {
            System.err.println("Unable to find primary keys not importing file !");
            return;
        }
        if (columnType.isEmpty()) {
            System.err.println("Unable to find column keys not importing file !");
            return;
        }
        int pos = 0;
        primaryKeysPos = new int[primaryKeys.length];

        for (int i = 0; i < primaryKeys.length; i++) {
            if (columns.contains(primaryKeys[i])) {
                primaryKeysPos[i] = columns.indexOf(primaryKeys[i]);
            } else {
                System.err.println("Oups primary key not among columns, stop importing");
                return;
            }
        }
        int reportRate = 10000;
        long lastReportTime=System.currentTimeMillis() ;

        if (initialize_cache(tableName)){
            int numofEntries =loaded_tuples.get(tableName);
            int lines = 0;
            String key="";
            System.out.println("Importing data ... ");
            long sizeE = 0;

            String keyLine = "";

            try {
                keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvfile)));
            } catch (FileNotFoundException e) {
                System.out.println("Unable to read keys file, skipping "+ tableName);
                e.printStackTrace();
                return;
            }

            Long currentStartTime = System.currentTimeMillis();
            while ((keyLine = keyReader.readLine()) != null){
                if (maxTuples>0 && numofEntries >= maxTuples){
                    System.out.println("Stopping import limit reached " + maxTuples + " " );
                    break;
                }
                JsonObject data = new JsonObject();

                // read line and values separated by commas
                String[] dataline = keyLine.split(",");

                for (pos = 0; pos < columns.size(); pos++) {
                    String fullCollumnName =  "default."+tableName+"." + columns.get(pos);
                    Class ct = columnType.get(pos);
                    try {
                        if (ct == String.class)
                            data.putString(fullCollumnName, dataline[pos]);
                        else if (ct == Long.class)
                            data.putNumber(fullCollumnName, Long.parseLong(dataline[pos]));
                        else if (ct == Integer.class)
                            data.putNumber(fullCollumnName,  Integer.parseInt(dataline[pos]));
                        else if (ct == Float.class)
                            data.putNumber(fullCollumnName, Float.parseFloat(dataline[pos]));
                        else{
                            System.err.println("Not recognised type, stop importing");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Line: " + lines + "Parsing error, put random generated float number");
                        data.putNumber(fullCollumnName, nextFloat(-3, 3));
                    }
                }

                // create key
                key = dataline[primaryKeysPos[0]];
                for (int i = 1; i < primaryKeysPos.length; i++) {
                    key += ":" + dataline[primaryKeysPos[i]];
                }

                put(key, data.toString());

                try {
                    sizeE+=serialize(data).length;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                numofEntries++;
                if (delay > 50)
                    System.out.println("Cache put: " + numofEntries);

                if (numofEntries % reportRate == 0) {
                    System.out.println("File Import (t/s):" + (float) reportRate / (float) ((System.currentTimeMillis() - lastReportTime) / 1000.0)+" Avg (t/s): " + (numofEntries - loaded_tuples.get(tableName)) / ((System.currentTimeMillis() - currentStartTime) / 1000.0) + " Imported: " + numofEntries + " size: " + sizeE + " Avg tpl size: " + sizeE/(numofEntries - loaded_tuples.get(tableName)));
                    lastReportTime=System.currentTimeMillis();
                }
            }
            keyReader.close();
            System.out.println("File Closed. Wait For All Puts");
            EnsembleCacheUtils.waitForAllPuts();
            System.out.println("File Imprt AvgRate(t/s): " + (numofEntries - loaded_tuples.get(tableName)) / ((System.currentTimeMillis() - currentStartTime) / 1000.0) + " Imported: " + numofEntries + "size: " + sizeE + " Avg tpl size: " + sizeE/(numofEntries - loaded_tuples.get(tableName))+ " file: " + csvfile);

            loaded_tuples.put(tableName, numofEntries);
            loading_times.put(tableName, loading_times.get(tableName) + (System.currentTimeMillis() - currentStartTime));
            System.out.println("Overall table "+ tableName+ " Duration: " +DurationFormatUtils.formatDuration(loading_times.get(tableName), "HH:mm:ss,SSS") + " Totally Imported: " + numofEntries +" Mean Rate(t/s) "+numofEntries/(float)(loading_times.get(tableName)/1000.0));

        }
    }

    public static byte[] serialize(JsonObject obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj.toString());
        return b.toByteArray();
    }

    public static float nextFloat(float min, float max) {
        return min + r.nextFloat() * (max - min);
    }

    private static void put(String key, String value) {
        Tuple tuple = new Tuple(value);
        if (remoteCache != null)
//            remoteCache.put(remoteCache.getName() + ":" + key, tuple);
            EnsembleCacheUtils.putToCache(remoteCache, remoteCache.getName() + ":" + key, tuple);
        else if (embeddedCache != null)
//            embeddedCache.put(((Cache) embeddedCache).getName() + ":" + key, tuple);
            EnsembleCacheUtils.putToCache(
                    (BasicCache) embeddedCache, ((Cache) embeddedCache).getName() + ":" + key, tuple);
        else if (ensembleCache!=null)
//            ensembleCache.put( ensembleCache.getName() + ":" + key, tuple);
            EnsembleCacheUtils.putToCache(ensembleCache, ensembleCache.getName() + ":" + key, tuple);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static boolean initialize_cache(String tableName) {

        System.out.println(" Tablename: " + tableName + " Trying to create cache: " + StringConstants.DEFAULT_DATABASE_NAME + "." + tableName);
        if (remoteCacheManager != null)
            try {
                remoteCache = remoteCacheManager.getCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName);
            } catch (Exception e) {
                System.err.println("Error " + e.getMessage() + " Terminating file loading.");
                return false;
            }
        else if (imanager != null)
            embeddedCache = imanager.getPersisentCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName);
        else if (emanager != null)
            ensembleCache = emanager.getCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName,new ArrayList<>(emanager.sites()),
                    EnsembleCacheManager.Consistency.DIST);
        else {
            System.err.println("Not recognised type, stop importing");
            return false;
        }


        if (embeddedCache == null && remoteCache == null && ensembleCache ==null) {
            System.err.println("Unable to create cache, exiting");
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

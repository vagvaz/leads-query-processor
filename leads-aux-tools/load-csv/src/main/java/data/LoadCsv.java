package data;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.sentiment.SentimentAnalysisModule;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static data.LoadCsv.plugs.PAGERANK;
import static data.LoadCsv.plugs.SENTIMENT;

/**
 * Created by vagvaz on 10/29/14.
 */
public class LoadCsv {
    enum plugs {SENTIMENT, PAGERANK};
    transient protected static Random r;

    static int delay = 0;
    static RemoteCacheManager remoteCacheManager = null;
    static InfinispanManager imanager = null;
    static EnsembleCacheManager emanager;
    static long all_bytes=0;
    static long all_records=0;
    static ConcurrentMap embeddedCache = null;
    static RemoteCache remoteCache = null;
    static EnsembleCache ensembleCache = null;
    static ArrayList<EnsembleCache>  ecaches = new ArrayList<>();
    static boolean ensemple_multi = false;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        r = new Random(0);

        if (args.length == 0) {
            System.out.print(" Syntax:\tconvertadd filename {inputcollumn conversion}+ \n where convertion type: sentiment, pagerank");
            System.err.println("or  \t\t$prog loadIspn dir (delay per put)\n ");
            System.err.println("or  \t\t$prog loadRemote dir host port (delay per put)\n ");
            System.err.println("or  \t\t$prog loadEnsemble dir host:port(|host:port)+ (delay per put)\n ");
            System.err.println("or  \t\t$prog loadEnsembleMulti dir host:port(|host:port)+ (delay per put)\n ");

            System.exit(-1);
        }
        if (args[0].startsWith("convert")) {
            convert_csv(args);
            System.exit(0);
        }
        LQPConfiguration.initialize();

        if (args[0].startsWith("l")) {
            if(args[0].equals("loadIspn")) {
                imanager = InfinispanClusterSingleton.getInstance().getManager();

            }else if(args[0].equals("loadRemote")) {
                if (args.length != 2 && args.length < 4) {
                    System.err.println("wrong number of arguments for load $prog load dir/ $prog load dir host port (delay per put)");
                    System.exit(-1);
                }
                String[] parts = args[2].split(":");
                String host = parts[0];
                String port = parts[1];
                remoteCacheManager = createRemoteCacheManager(host, port);
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
                System.out.println("Using ensemble sring " + ensembleString);
                emanager = new EnsembleCacheManager((ensembleString));
                System.out.println("Emanager has " + emanager.sites().size() + " sites");
                emanager.start();
            }else{
                System.exit(0);
            }

            loadData(args[1]);
        }
    }

    private static void convert_csv(String[] args) {

        Long filestartTime = System.currentTimeMillis();
        String initfilename = args[1];
        System.out.print("Trying to convert file: " + initfilename);
        String filename[] = initfilename.split(".csv");
        //System.out.println("Filename" + csvfile.getAbsolutePath()+" "+filename[0]);

        String fulltableName[] = (initfilename.split(".csv")[0]).split("-");
        String tableName = fulltableName[fulltableName.length - 1];
        String keysFilename = filename[0] + ".keys";
        Path path = Paths.get(keysFilename);
        SentimentAnalysisModule sentimentAnalysisModule = null;

        if (args.length % 2 != 0) {
            System.err.print("Not enougth arguments, Syntax: convertadd filename {inputcollumn conversion}+ \n where convertion type: sentiment, pagerank");
        }

        HashMap<plugs, Integer> plugins = new HashMap<>();
        HashMap<plugs, Integer> output = new HashMap<>();

        int max_column = 0;
        String inputcolumn, outputcolumn;
        int incolumn = 0, outcolumn = 0;
        for (int i = 2; i < args.length; i = i + 2) {
            incolumn = 0;
            inputcolumn = args[i + 1];
            if (inputcolumn.contains(":")) {
                String[] iocol = inputcolumn.split(":");
                if (iocol.length == 2) {
                    inputcolumn = iocol[0];
                    outcolumn = Integer.parseInt(iocol[1]);
                } else
                    System.err.print("bad input out put column error " + inputcolumn);
            }

            incolumn = Integer.parseInt(inputcolumn);
            if (incolumn > max_column)
                max_column = incolumn;
            if (args[i].startsWith("sentiment")) {
                plugins.put(SENTIMENT, incolumn - 1);
                //output.put(SENTIMENT,outcolumn-1);
                //sentimentAnalysisModule = new SentimentAnalysisModule("../classifiers/english.all.3class.distsim.crf.ser.gz");
            } else if (args[i].startsWith("pagerank")) {
                plugins.put(PAGERANK, incolumn - 1);
                //output.put(PAGERANK,outcolumn-1);
            } else {
                System.err.print("Unknown plugin!!!" + args[i]);
            }
        }

        SentimentAnalysisModule module;
        HashSet<Integer> errorenousline = new HashSet<Integer>();
        try {
            CSVReader reader = new CSVReader(new FileReader(initfilename));

            String outputfn = initfilename.split(".csv")[0] + "-tuc.csv";
            String errinitfilename = initfilename + "errlines";
            path = Paths.get(errinitfilename);
            CSVReader reader2 = null;
            if (Files.exists(path)) {
                reader2 = new CSVReader(new FileReader(errinitfilename));
                String[] errline;
                while ((errline = reader2.readNext()) != null) {
                    errorenousline.add(Integer.parseInt(errline[0]));
                }
                reader2.close();
            }

            int convertedrows = 0;
            int alreadyconvertedrows = 0;
            path = Paths.get(outputfn);

            CSVWriter writer;
            if (Files.exists(path)) {

                reader2 = new CSVReader(new FileReader(outputfn));
                while (reader2.readNext() != null) {
                    reader.readNext();
                    convertedrows++;
                }
                reader2.close();
                for (Integer e : errorenousline) {
                    if (convertedrows > e) {
                        reader.readNext();
                        convertedrows++;
                    }
                }

                System.out.println("Continue from row: " + convertedrows);
                alreadyconvertedrows = convertedrows;
                filestartTime = System.currentTimeMillis();
                writer = new CSVWriter(new FileWriter(outputfn, true));
            } else
                writer = new CSVWriter(new FileWriter(outputfn));


            String[] StringData;

            StringData = reader.readNext();
            String[] newStringData = new String[StringData.length + plugins.size()];

            if (StringData.length < max_column) {
                System.err.println("Columns size < maximum column number at import error, stop converting");
                return;
            }
            int data_lenght = StringData.length;
            int pagerank = 0;
            String content;
            int maximumSentimentStringLength = 600;
            int cutoffchars = 0;
            int allchars = 0;
            do {
                if (errorenousline.contains(convertedrows)) {
                    System.out.println("Skipping line: " + convertedrows);
                } else {

                    System.arraycopy(StringData, 0, newStringData, 0, data_lenght);
                    int counter = data_lenght;
                    int index;
                    String newValue;
                    for (Map.Entry<plugs, Integer> e : plugins.entrySet()) {
                        newValue = "0";
                        if (output.containsKey(e.getKey()))
                            index = output.get(e.getKey());
                        else
                            index = counter++;
                        if (index < data_lenght && index < StringData.length) {//check if value already exists
                            if (!StringData[index].isEmpty())
                                continue;
                        }

                        if (PAGERANK == e.getKey()) {
                            //Thread.sleep(500);
                            //pagerank = Web2.pagerank(transformUri(StringData[e.getValue()]));
                            //if(pagerank<0)
                            pagerank = r.nextInt(8);
                            //else
                            //Thread.sleep(300);

                            //System.out.println(" pagerank: " + pagerank);
                            newValue = String.valueOf(pagerank);
                        } else if (e.getKey() == SENTIMENT) {
                            try {
                                content = StringData[e.getValue()];
                                allchars += content.length();
                                if (content.length() > maximumSentimentStringLength) {
                                    cutoffchars += content.length() - maximumSentimentStringLength;
                                    content = content.substring(0, maximumSentimentStringLength);
                                }
                                /*newStringData[counter++]*/
                                newValue = String.valueOf(nextFloat(-5, 5)); // String.valueOf(sentimentAnalysisModule.getOverallSentiment(content).getValue());
                            } catch (StackOverflowError er) {
//                                newStringData[counter++] = "0";
                                newValue = "0";
                                CSVWriter errorwriter = new CSVWriter(new FileWriter(errinitfilename, true));
                                String[] err = new String[1];
                                err[0] = String.valueOf(convertedrows);
                                errorwriter.writeNext(err);
                                errorwriter.close();
                                errorenousline.add(convertedrows);
                                writer.flush();
                            }
                        }


                        newStringData[index] = newValue;
                    }
                    writer.writeNext(newStringData);

                    if (convertedrows % 100 == 0) {
                        System.out.print("Converted " + convertedrows + " Mean process time: " + DurationFormatUtils.formatDuration((long) ((System.currentTimeMillis() - filestartTime) / (float) (convertedrows - alreadyconvertedrows + 1)), "HH:mm:ss,SSS"));
                        //System.out.println(" bad: " + errorenousline.size() + " charout: " + cutoffchars + " "+((float)cutoffchars/allchars)*100+ "%");
                        System.out.print("\n");
                        System.out.flush();
                        writer.flush();
                    }
                }
                convertedrows++;
            } while ((StringData = reader.readNext()) != null);
            writer.flush();
            writer.close();
            System.out.println("Loading time: " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - filestartTime, "HH:mm:ss,SSS"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }


    private static void loadData(String path) throws IOException, ClassNotFoundException {

        Long startTime = System.currentTimeMillis();
        Path dir = Paths.get(path);
        List<File> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{csv}")) {
            for (Path entry : stream) {
                files.add(entry.toFile());
            }
        } catch (IOException x) {
            throw new RuntimeException(String.format("error reading folder %s: %s",
                    dir,
                    x.getMessage()),
                    x);
        }
        for (File csvfile : files) {
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
        System.out.println("Overall Folder Loading time: " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss,SSS"));
        System.out.println("Imported: " + all_records+ " records, In memory bytes(not raw):" + all_bytes + ", Average: " +((float)all_records/(float)all_bytes));

        System.exit(0);
    }

    private static void loadDataFromFile(File csvfile) throws IOException {
        String filename[] = csvfile.getAbsolutePath().split(".csv");
        //System.out.println("Filename" + csvfile.getAbsolutePath()+" "+filename[0]);

        String fulltableName[] = (csvfile.getName().split(".csv")[0]).split("-");
        String tableName = fulltableName[fulltableName.length - 1];
        String keysFilename = filename[0] + ".keys";
        Path path = Paths.get(keysFilename);

        BufferedReader keyReader = null;
        if (Files.exists(path)) {
            try {
                keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(keysFilename)));
            } catch (FileNotFoundException e) {
                System.out.println("Unable to read keys file, skipping " + filename[0] + ".csv");
                e.printStackTrace();
                return;
            }
            System.out.println(" Loading key from file " + filename[0] + ".keys");
        } else {
            System.err.println(" No keys file, skipping " + filename[0] + ".csv");
            return;
        }
        //Read the keys

        ArrayList<Class> columnType = new ArrayList<>();
        ArrayList<String> columns = new ArrayList<>();
        //HashSet<String > primaryKeys = new HashSet<String>();
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
                        System.out.print("Must find #" + keysTypePairs.length + " column names, ");
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
                                    columnType.add(Integer.class);
                                else if (pair[1].toLowerCase().equals("float"))
                                    columnType.add(Float.class);
                                else {
                                    System.err.print("Column Key not recognized type: " + pair[1]);
                                    continue;
                                }
                            }
                        }
                        System.out.println("Recognized Columns #" + keysTypePairs.length);
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

        if (initialize_cache(tableName))
            try {

                CSVReader reader = new CSVReader(new FileReader(csvfile), ',');
                String valueLine = "";
                int numofEntries = 0;
                int numofBytes = 0;
                int numofChars = 0;

                int lines = 0;
                String[] StringData;
                System.out.println("Importing data ... ");
                long sizeE = 0;
                // cache.startBatch();
                while ((StringData = reader.readNext()) != null) {
                    lines++;
                    if (StringData.length != columns.size()) {

                        System.err.println("Line: " + lines + " Columns size: " + columns.size() + ", data column:" + StringData.length + " size mismatch, continue importing");
                        continue;
                    }
                    BasicBSONObject data = new BasicBSONObject();
                    String key = StringData[primaryKeysPos[0]];
                    for (int i = 1; i < primaryKeysPos.length; i++) {
                        key += ":" + StringData[primaryKeysPos[i]];
                    }

                    for (pos = 0; pos < StringData.length; pos++) {
                        numofChars+=StringData[pos].length();

                        String fullCollumnName =  "default."+tableName+"." + columns.get(pos);
                        if (columnType.get(pos) == String.class)
                            /*if (columns.get(pos).equals("textcontent") || ta  bleName == "page_core")
                                data.putString(fullCollumnName, "");
                            else*/
                                data.put(fullCollumnName, StringData[pos]);
                        else try {
                            if (columnType.get(pos) == Long.class)
                                data.put(fullCollumnName, Long.parseLong(StringData[pos]));
                            else if (columnType.get(pos) == Integer.class)
                                data.put(fullCollumnName, Integer.parseInt(StringData[pos]));
                            else if (columnType.get(pos) == Float.class) {
                                float num = Float.parseFloat(StringData[pos]);

                                if (Float.isNaN(num)) {
                                    num = nextFloat(-5, 5);
                                    System.err.println("Found " + StringData[pos] + " .. ->  " + num);
                                }
                                data.put(fullCollumnName, num);
                            } else {
                                System.err.println("Not recognised type, stop importing");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Line: " + lines + "Parsing error: " + StringData[pos]);
                            //e.printStackTrace();
                            data.put(fullCollumnName, nextFloat(-3, 3));
                        }

                    }

                    put(key, data);
                    numofBytes += key.getBytes().length+data.toString().getBytes().length;

                    try {
                        sizeE+=data.toString().length();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    numofEntries++;
                    if (delay > 50) {
                        System.out.println("Cache put: " + numofEntries);
                    }
                    if (numofEntries % 1000 == 0) {
                        System.out.println("Imported: " + numofEntries + ", Charbytes: " + numofChars + ", bytes: " + numofBytes + ", average: " + numofBytes / numofEntries);
                        System.out.println("Imported: " + numofEntries + " -- size: " + sizeE);
                        //cache.endBatch(true);
                    }
//                   if(numofEntries%300==0){
//                                 return;
//                    }
                }
                all_bytes +=numofBytes;
                all_records+=numofEntries;
                System.out.println("Totally Imported: " + numofEntries + ", Charbytes: " + numofChars +", bytes: " + numofBytes + ", average: " + numofBytes/numofEntries);
                EnsembleCacheUtils.waitForAllPuts();
                EnsembleCacheUtils.waitForAuxPuts();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


    }

//    public static byte[] serialize(JsonObject obj) throws IOException {
////        ByteArrayOutputStream b = new ByteArrayOutputStream();
////        ObjectOutputStream o = new ObjectOutputStream(b);
////        o.writeObject(obj.toString());
////        return b.toByteArray();
//    }

    private static void put(String key, BSONObject value) {
        Tuple tuple = new Tuple(value);
        if (remoteCache != null)
            remoteCache.put(remoteCache.getName() + ":" + key, tuple);
        else if (embeddedCache != null)
            embeddedCache.put(((Cache) embeddedCache).getName() + ":" + key, tuple);
        else if (ensembleCache!=null) {
            EnsembleCacheUtils.putToCacheDirect(ensembleCache, ensembleCache.getName() + ":" + key, tuple);
//            try {
//                EnsembleCacheUtils.waitForAuxPuts();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
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
//            if(ensemple_multi)
                ensembleCache = emanager.getCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName,new ArrayList<>(emanager.sites()),
                        EnsembleCacheManager.Consistency.DIST);
//            else
//                ensembleCache = emanager.getCache(StringConstants.DEFAULT_DATABASE_NAME + "." + tableName);
        else {
            System.err.println("Not recognised type, stop importing");
            return false;
        }


        if (embeddedCache == null && remoteCache == null && ensembleCache ==null) {
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

    private static String transformUri(String nutchUrlBase) {

        String domainName = "";
        String url = "";

        String[] parts = nutchUrlBase.split(":");
        String nutchDomainName = parts[0];

        String[] words = nutchDomainName.split("\\.");

        for (int i = words.length - 1; i >= 0; i--) {
            domainName += words[i] + ".";
        }
        domainName = domainName.substring(0, domainName.length() - 1);

        if (parts.length == 2) {
            //System.out.print("Parts[1]:" + parts[1]);
            String[] parts2 = parts[1].split("/");
            if (parts2[0].startsWith("http")) ;
            url = parts2[0] + "://" + domainName;
            for (int i = 1; i < parts2.length; i++) {
                url += "/" + parts2[i];
            }
        }
        //System.out.print("Corrected url: " +  url);
        return url;

    }

    public static float nextFloat(float min, float max) {
        return min + r.nextFloat() * (max - min);
    }

//    protected String getRandomDomain() {
//        int l = 10;
//        String result = "";
//        for (int i = 0; i < l; i++) {
//            result += loc[r.nextInt(loc.length)];
//        }
//        return "www." + result + ".com";
//    }

}

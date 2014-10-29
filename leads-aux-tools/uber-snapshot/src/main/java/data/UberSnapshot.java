package data;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.data.Snapshot;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.util.CloseableIterable;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vagvaz on 10/29/14.
 */
public class UberSnapshot {

   static RemoteCacheManager manager;
   static InfinispanManager imanager;
   public static void main(String[] args) throws IOException, ClassNotFoundException {
      if(args.length != 2  && args.length != 4)
      {
         System.err.println("Wrong number of arguments");
         System.err.println("program [store/load] directory host port (only in load)");
         System.exit(-1);
      }
      LQPConfiguration.initialize();
      imanager = InfinispanClusterSingleton.getInstance().getManager();
      if(args[0].startsWith("l")){
         loadData(args);
      }
      else{
         storeData(args);
      }
   }

   private static void storeData(String[] args) throws IOException {
      if(args.length != 2 ){
         System.err.println("wrong number of arguments for store $prog store dir");
         System.exit(-1);
      }


      System.out.println("storing webpages");
      store("default.webpages",args[1]);
      System.out.println("storing entities");
      store("default.entities",args[1]);
      System.out.println("storing pagerankCache");
      storePagerank(args[1]);
      System.out.println("storing approx sum");
      storeApproxSum(args[1]);

   }

   private static void store(String s, String arg) throws IOException {
      String cacheName = s;

      Cache cache = (Cache) imanager.getPersisentCache(cacheName);
      if(cache.size() == 0){
         System.out.println("cache size is 0");
         return;
      }
      FileWriter keyOut = new FileWriter(arg+"/"+s+".keys");
      FileWriter sizeOut = new FileWriter(arg+"/"+s+".sizes");
      FileWriter valueOut = new FileWriter(arg+"/"+s+".values");


      FileWriter writer;

      CloseableIterable<Map.Entry<String, String>> iterable =
              cache.getAdvancedCache().filterEntries(new AcceptAllFilter());
      long counter = 0;
      for (Map.Entry<String, String> entry : iterable) {
         keyOut.write(entry.getKey()+"\n");
         valueOut.write(entry.getValue()+"\n");
         System.out.println(counter++);
      }
      keyOut.close();
      valueOut.close();
      System.out.println("Stored " + counter + " tuples ");
   }

   private static void storeApproxSum(String dir) throws IOException {
      String cacheName = "approx_sum_cache";
      Cache cache = (Cache) imanager.getPersisentCache(cacheName);
      if(cache.size() == 0){
         System.out.println("cache size is 0");
         return;
      }
      FileWriter keyOut = new FileWriter(dir+"/"+"approx_sum_cache"+".keys");
//      FileWriter sizeOut = new FileWriter(dir+"/"+"approx_sum_cache"+".keys");
      FileWriter valueOut = new FileWriter(dir+"/"+"approx_sum_cache"+".values");


      FileWriter writer;

      CloseableIterable<Map.Entry<String, Integer>> iterable =
              cache.getAdvancedCache().filterEntries(new AcceptAllFilter());
      long counter = 0;
      for (Map.Entry<String, Integer> entry : iterable) {
         keyOut.write(entry.getKey()+"\n");
         valueOut.write(entry.getValue().toString()+"\n");
         System.out.println(counter++);
      }
      keyOut.close();
      valueOut.close();
      System.out.println("Stored " + counter + "approx_sums tuples ");
   }

   private static void storePagerank(String dir) throws IOException {
      String cacheName = "pagerankCache";
      LQPConfiguration.initialize();
      Cache cache = (Cache) imanager.getPersisentCache(cacheName);
      if(cache.size() == 0){
         System.out.println("cache size is 0");
         return;
      }
      FileWriter keyOut = new FileWriter(dir+"/"+"pagerankCache"+".keys");
//      FileWriter sizeOut = new FileWriter(dir+"/"+"pagerankCache"+".sizes");
//      FileWriter valueOut = new FileWriter(dir+"/"+"pagerankCache"+".values");
      ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(dir+"/"+"pagerankCache"+".values"));


      FileWriter writer;

      CloseableIterable<Map.Entry<String, DSPMNode>> iterable =
              cache.getAdvancedCache().filterEntries(new AcceptAllFilter());
      long counter = 0;
      for (Map.Entry<String, DSPMNode> entry : iterable) {
         keyOut.write(entry.getKey()+"\n");
         DSPMNode tmp = entry.getValue();
//         int zero = tmp.getFipVisits();
//         outstream.writeInt(zero);
         int one = tmp.getDspmVisits();
         outstream.writeInt(one);
         int two = tmp.getPend();
         outstream.writeInt(two);

         int four = tmp.getFipVisits();
         outstream.writeInt(four);
         TObjectIntHashMap five = tmp.getStepChoices();
         five.writeExternal(outstream);
         THashMap<Object, TreeMap<Integer, Object>> six = tmp.getFip_map();
         six.writeExternal(outstream);
         THashSet seven = tmp.getNeighbours();
         seven.writeExternal(outstream);
         System.out.println(counter++);
      }
      keyOut.close();
      outstream.close();
      System.out.println("Stored " + counter + "approx_sums tuples ");
   }

   private static void loadData(String[] args) throws IOException, ClassNotFoundException {
      if(args.length != 2 && args.length != 4 ){
         System.err.println("wrong number of arguments for store $prog load dir/ $prog load dir host port");
         System.exit(-1);
      }

      System.out.println("Loading entties remotely");
      if(args.length > 2 ){
         loadDataWithRemote(args);
      }else{
         loadDataEmbedded(args);
      }
      System.out.println("loading fin");



   }

   private static void loadDataWithRemote(String[] args) throws IOException, ClassNotFoundException {
      manager = createRemoteCacheManager(args[2],args[3]);
      System.out.println("loading webpages");
      loadCacheTo("default.webpages", args[1]);
      System.out.println("loading entities");
      loadCacheTo("default.webpages", args[1]);

      Cache cache = (Cache) manager.getCache("approx_sum_cache");
      loadApproxSum(args[1], cache);

      Cache cachep  = (Cache) manager.getCache("pagerankCache");
      loadPagerank(args[1],cachep);
   }

   private static void loadCacheTo(String s, String arg) throws IOException {
      String cacheName = s;
      Cache cache = (Cache) manager.getCache(cacheName);
      BufferedReader keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(arg+"/"+cacheName+".keys")));
//        BufferedReader sizeReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".sizes")));
      BufferedReader valueReader = new BufferedReader(new InputStreamReader(new FileInputStream(arg+"/"+cacheName+".values")));

      String keyLine = "";
      String valueLine = "";

      try {
         keyLine = keyReader.readLine();
         valueLine = valueReader.readLine();
      } catch (IOException e) {
         keyReader.close();
         valueReader.close();
         System.out.println("Emtpy files ?");
      }
      long counter = 0;
      try {
         while (true && keyLine != null){
            if(keyLine != null && !keyLine.trim().equals("")){
               if(valueLine != null && !valueLine.trim().equals("")) {
                  JsonObject ob = new JsonObject(valueLine);
                  cache.put(keyLine.trim(), valueLine.trim());
               }
            }

            System.out.println(counter++);
            keyLine = keyReader.readLine();
            valueLine = valueReader.readLine();
         }
      }catch(IOException e){
         keyReader.close();
         valueReader.close();
         System.out.println("Read " + counter + "tuples");
      }
   }
   private static void loadDataEmbedded(String[] args) throws IOException, ClassNotFoundException {
      System.out.println("loading webpages");
         loadCache("default.webpages",args[1]);
      System.out.println("loading entities");
      loadCache("default.webpages",args[1]);

      Cache cache = (Cache) imanager.getPersisentCache("approx_sum_cache");
      loadApproxSum(args[1], cache);
      cache = (Cache) imanager.getPersisentCache("pagerankCache");
      loadPagerank(args[1],cache);
   }
   private static void loadPagerank(String arg,Cache cache) throws IOException, ClassNotFoundException {
      String cacheName = "pagerankCache";

      BufferedReader keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(arg+"/"+cacheName+".keys")));
//        BufferedReader sizeReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".sizes")));
      ObjectInputStream instream = new ObjectInputStream(new FileInputStream(arg+"/"+cacheName+".values"));



      String keyLine = "";
      DSPMNode tmp = new DSPMNode("");


      try {
         keyLine = keyReader.readLine();

//         int zero = instream.readInt();
         int one = instream.readInt();
         int two = instream.readInt();
         int three = instream.readInt();
         int four = instream.readInt();
         TObjectIntHashMap five = new TObjectIntHashMap();
         five.readExternal(instream);
         THashMap<Object, TreeMap<Integer, Object>> six = new THashMap<>();
         THashSet seven = new THashSet();
//         tmp.setFipVisits(zero);
         tmp.setDspmVisits(one);
         tmp.setPend(two);
         tmp.setFipVisits(four);
         tmp.setStepChoices(five);
         tmp.setFip_map(six);
         tmp.setNeighbours(seven);
         cache.put(keyLine.trim(),tmp);
         tmp = new DSPMNode("");
      } catch (IOException e) {
         keyReader.close();
         instream.close();
         System.out.println("Emtpy files ?");
      }
      long counter = 0;
      try {
         while (true && keyLine != null){
            if(keyLine != null && !keyLine.trim().equals("")){
               keyLine = keyReader.readLine();

//         int zero = instream.readInt();
               int one = instream.readInt();
               int two = instream.readInt();
               int three = instream.readInt();
               int four = instream.readInt();
               TObjectIntHashMap five = new TObjectIntHashMap();
               five.readExternal(instream);
               THashMap<Object, TreeMap<Integer, Object>> six = new THashMap<>();
               THashSet seven = new THashSet();
//         tmp.setFipVisits(zero);
               tmp.setDspmVisits(one);
               tmp.setPend(two);
               tmp.setFipVisits(four);
               tmp.setStepChoices(five);
               tmp.setFip_map(six);
               tmp.setNeighbours(seven);
               cache.put(keyLine.trim(),tmp);
               tmp = new DSPMNode("");
            }

            counter++;
            keyLine = keyReader.readLine();

         }
      }catch(IOException e){
         keyReader.close();
         instream.close();
         System.out.println("Read " + counter + "tuples");
      }
   }
   private static void loadApproxSum(String arg, Cache cache) throws IOException {
      String cacheName = "approx_sum_cache";

      BufferedReader keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(arg+"/"+cacheName+".keys")));
//        BufferedReader sizeReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".sizes")));
      BufferedReader valueReader = new BufferedReader(new InputStreamReader(new FileInputStream(arg+"/"+cacheName+".values")));



      String keyLine = "";
      String valueLine = "";

      try {
         keyLine = keyReader.readLine();
         valueLine = valueReader.readLine();
      } catch (IOException e) {
         keyReader.close();
         valueReader.close();
         System.out.println("Emtpy files ?");
      }
      long counter = 0;
      try {
         while (true && keyLine != null){
            if(keyLine != null && !keyLine.trim().equals("")){
               if(valueLine != null && !valueLine.trim().equals("")) {
                  Integer realvalue = Integer.parseInt(valueLine);
                  cache.put(keyLine.trim(), realvalue);
                  System.out.println(counter++);
               }
            }

            counter++;
            keyLine = keyReader.readLine();
            valueLine = valueReader.readLine();
         }
      }catch(IOException e){
         keyReader.close();
         valueReader.close();
         System.out.println("Read " + counter + "tuples");
      }
   }

   private static void loadCache (String cacheName, String dir) throws IOException {
      Cache cache = (Cache) imanager.getPersisentCache(cacheName);
      BufferedReader keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".keys")));
//        BufferedReader sizeReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".sizes")));
      BufferedReader valueReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".values")));



      String keyLine = "";
      String valueLine = "";

      try {
         keyLine = keyReader.readLine();
         valueLine = valueReader.readLine();
      } catch (IOException e) {
         keyReader.close();
         valueReader.close();
         System.out.println("Emtpy files ?");
      }
      long counter = 0;
      try {
         while (true && keyLine != null){
            if(keyLine != null && !keyLine.trim().equals("")){
               if(valueLine != null && !valueLine.trim().equals("")) {
                  JsonObject ob = new JsonObject(valueLine);
                  cache.put(keyLine.trim(), valueLine.trim());
                  System.out.println(counter++);
               }
            }

            counter++;
            keyLine = keyReader.readLine();
            valueLine = valueReader.readLine();
         }
      }catch(IOException e){
         keyReader.close();
         valueReader.close();
         System.out.println("Read " + counter + "tuples");
      }
   }
   private static void loadFromTo(String cacheName, String dir, String host, String port) throws IOException {

      LQPConfiguration.initialize();
//      InfinispanManager manager = InfinispanClusterSingleton.getInstance().getManager();
      RemoteCacheManager manager = createRemoteCacheManager(host,port);
      RemoteCache cache =  manager.getCache(cacheName,true);
      BufferedReader keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".keys")));
//        BufferedReader sizeReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".sizes")));
      BufferedReader valueReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+cacheName+".values")));

      String keyLine = "";
      String valueLine = "";

      try {
         keyLine = keyReader.readLine();
         valueLine = valueReader.readLine();
      } catch (IOException e) {
         keyReader.close();
         valueReader.close();
         System.out.println("Emtpy files ?");
      }
      long counter = 0;
      try {
         while (true && keyLine != null){
            if(keyLine != null && !keyLine.trim().equals("")){
               if(valueLine != null && !valueLine.trim().equals("")) {
                  JsonObject ob = new JsonObject(valueLine);
                  cache.put(keyLine.trim(), valueLine.trim());
               }
            }

            System.out.println(counter++);
            keyLine = keyReader.readLine();
            valueLine = valueReader.readLine();
         }
      }catch(IOException e){
         keyReader.close();
         valueReader.close();
         System.out.println("Read " + counter + "tuples");
      }
   }


   private static RemoteCacheManager createRemoteCacheManager(String host, String port) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(host).port(Integer.parseInt(port));
      return new RemoteCacheManager(builder.build());
   }
}

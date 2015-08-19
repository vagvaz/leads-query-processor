package eu.leads.processor.core;
import org.bson.BasicBSONEncoder;
import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by vagvaz on 8/17/15.
 */
public class LevelDBIndex {

    private static Tuple t;
    private WriteOptions writeOptions;
    private DB keysDB;
    private DB dataDB;
    private File baseDirFile;
    private File keydbFile;
    private File datadbFile;
    private Options options;
    private LevelDBIterator keyIterator;
    private LevelDBDataIterator valuesIterator;
    private int batchSize = 18000;
    private int batchCount =0;
    private WriteBatch batch;
    private WriteBatch keyBatch;
    //    private BasicBSONEncoder encoder = new BasicBSONEncoder();

    public LevelDBIndex(String baseDir, String name){
        baseDirFile = new File(baseDir);
        if(baseDirFile.exists() && baseDirFile.isDirectory()){
            for(File f : baseDirFile.listFiles()){
                f.delete();
            }
            baseDirFile.delete();
        }else if(baseDirFile.exists()){
            baseDirFile.delete();
        }
        baseDirFile.mkdirs();
        keydbFile = new File(baseDirFile.toString()+"/keydb");
        datadbFile = new File(baseDirFile.toString()+"/datadb");
        options = new Options();
        options.writeBufferSize( 128*1024*1024);
        options.paranoidChecks(false);

        options.createIfMissing(true);
        //        options.blockSize(LQPConfiguration.getInstance().getConfiguration()
        //            .getInt("leads.processor.infinispan.leveldb.blocksize", 16)*1024*1024);
        //        options.cacheSize(LQPConfiguration.getInstance().getConfiguration()
        //            .getInt("leads.processor.infinispan.leveldb.cachesize", 256)*1024*1024);
        options.blockSize(16*1024 * 1024);

//        options.compressionType(CompressionType.SNAPPY);
        options.cacheSize( 160 * 1024*1024);
        try {
            keysDB = factory.open(keydbFile,options);
            dataDB = factory.open(datadbFile,options);
            writeOptions = new WriteOptions();
            writeOptions.sync(false);
            batch = dataDB.createWriteBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public  void printKeys(){
        DBIterator iterator = keysDB.iterator();
        iterator.seekToFirst();
        while(iterator.hasNext()){
            System.out.println(asString(iterator.next().getKey()));
        }

        iterator = dataDB.iterator();
        iterator.seekToFirst();
        while(iterator.hasNext()){
            System.out.println(asString(iterator.next().getKey()));
        }
        System.out.println("values-------------\n");
    }
    public Iterable<Map.Entry<String,Integer>> getKeysIterator(){
        keyIterator = new LevelDBIterator(keysDB);
        return keyIterator;
    }
    public Iterator<Object> getKeyIterator(String key , Integer counter){
//        if(valuesIterator == null){
            valuesIterator = new LevelDBDataIterator(dataDB,key,counter);
//        }

        valuesIterator.initialize(key,counter);
        return valuesIterator;

    }

    public static void main(String[] args) {
        LevelDBIndex index = new LevelDBIndex("/tmp/testdb/","mydb");
        initTuple();
        int numberofkeys = 200000;
        int numberofvalues = 2;
        String baseKey= "baseKeyString";

        long start = System.nanoTime();
        ArrayList<String> tuples = generate(numberofkeys,numberofvalues);
        System.out.println("insert");
        for(String k : tuples){
            //            System.out.println("key " + key);
            //            for(int value =0; value < numberofvalues; value++){
            index.add(k,t);
            //            }
        }
        index.flush();
        long end = System.nanoTime();
        long dur = end - start;
        dur /= 1000000;
        int total = numberofkeys*numberofvalues;
        double avg = total/(double)dur;

        System.out.println("Put " + (total) + " in " + (dur) + " avg " + avg);
        int counter  =0;

//               index.printKeys();

        start = System.nanoTime();
        //        for(int key = 0; key < numberofkeys; key++) {
        int totalcounter= 0;
        for(Map.Entry<String,Integer> entry : index.getKeysIterator()){
            counter = 0;
            //            System.out.println("iter key "+entry.getKey());
            Iterator<Object> iterator = index.getKeyIterator(entry.getKey(),entry.getValue());
            while(true){
                try {
                    Tuple t = (Tuple) iterator.next();
                    //                    String t = (String)iterator.next();
                    //                System.out.println(t.getAttribute("key")+" --- " + t.getAttribute("value"));
                    counter++;
                    totalcounter++;
                }catch(NoSuchElementException e){
                    break;
                }
            }
            if(counter != numberofvalues){
                System.err.println("Iteration failed for key " + entry.getKey() + " c " + counter);
            }
        }
        end = System.nanoTime();
        dur = end - start;
        dur /= 1000000;
        avg = total/(double)dur;
        System.out.println("Iterate " + (totalcounter) + " in " + (dur) + " avg " + avg);
        index.close();
        System.out.println("exit---");
    }

    public synchronized void flush() {
        try {

            dataDB.write(batch,writeOptions);
            batch.close();
            batch = dataDB.createWriteBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        printKeys();

    }

    private static ArrayList<String> generate(int numberofkeys, int numberofvalues) {
        String baseKey= "baseKeyString";
        System.out.println("generate");
        ArrayList<String> result = new ArrayList<>(numberofkeys*numberofvalues);
        for(int key = 0; key < numberofkeys; key++){
            //            System.out.println("key " + key);
            for(int value =0; value < numberofvalues; value++){
                result.add(baseKey+key);
            }
        }
        Collections.shuffle(result);
        return result;
    }

    private static void initTuple(){
        t = new Tuple();
        int key = 4;
        int value = 5;
        for(int i = 0 ; i < 10; i++){
            t.setAttribute("key-"+key+"-"+i,key);
            t.setAttribute("value-"+value+"-"+i,value);
            t.setAttribute("keyvalue-"+key+"."+value+"-"+i,key*value);
        }
    }
    private static Tuple getTuple(int key, int value) {

        //        t = new Tuple();
        //        int key = 4;
        //        int value = 5;
        //        t.setAttribute("key","baseKey"+Integer.toString(key));
        //        t.setAttribute("value",Integer.toString(value));
        //        for(int i = 0 ; i < 4; i++){
        //            t.setAttribute("key-"+key+"-"+i,key);
        //            t.setAttribute("value-"+value+"-"+i,value);
        //            t.setAttribute("keyvalue-"+key+"."+value+"-"+i,key*value);
        //        }
        return t;
    }

    public void put(Object key,Object value){
        add(key,value);
    }
    public synchronized void add(Object keyObject , Object valueObject){
        String key = null;
        Tuple value = null;

        key = keyObject.toString();
        value = (Tuple) valueObject;
        byte[] count = keysDB.get(bytes(key+"{}"));
        Integer counter = -1;
        if(count == null)
        {
            counter = 0;
        }
        else{
            ByteBuffer bytebuf = ByteBuffer.wrap(count);
            counter = bytebuf.getInt();
            counter += 1;
        }
        byte[] keyvalue = ByteBuffer.allocate(4).putInt(counter).array();
        keysDB.put(bytes(key+"{}"), keyvalue,writeOptions);
        //        encoder = new BasicBSONEncoder();
        BasicBSONEncoder encoder = new BasicBSONEncoder();
        byte[] b = encoder.encode(value.asBsonObject());

        //        System.out.println(b.length);
        //        dataDB.put(bytes(key+"{}"+counter),b,writeOptions);
        batch.put(bytes(key+"{}"+counter),b);
        batchCount++;
        if(batchCount>= batchSize){
            try {
                dataDB.write(batch,writeOptions);
                batch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            batch = dataDB.createWriteBatch();
            batchCount = 0;
        }

    }


    //

    public void close() {
        if(keyIterator != null){
            keyIterator.close();
        }
        if(valuesIterator!= null){
            valuesIterator.close();
        }
        if(keysDB != null){
            try {
                keysDB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dataDB != null){
            try {
                batch.close();
                dataDB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(File f : keydbFile.listFiles())
        {
            f.delete();
        }
        keydbFile.delete();

        for(File f : datadbFile.listFiles())
        {
            f.delete();
        }
        datadbFile.delete();

        baseDirFile = new File(baseDirFile.toString()+"/");
        for(File f : baseDirFile.listFiles())
        {
            f.delete();
        }
        baseDirFile.delete();

    }
}

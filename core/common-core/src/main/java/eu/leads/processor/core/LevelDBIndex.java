package eu.leads.processor.core;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.infinispan.ComplexIntermediateKey;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vagvaz on 8/17/15.
 */
public class LevelDBIndex {

    private static Tuple t;
    private DB keysDB;
    private DB dataDB;
    private File baseDirFile;
    private File keydbFile;
    private File datadbFile;
    private Options options;
    private LevelDBIterator keyIterator;
    private LevelDBDataIterator valuesIterator;
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
        keydbFile = new File(baseDirFile.toString()+"/keydb");
        datadbFile = new File(baseDirFile.toString()+"/datadb");
        options = new Options();
        options.createIfMissing(true);
        options.blockSize(LQPConfiguration.getInstance().getConfiguration()
            .getInt("leads.processor.infinispan.leveldb.blocksize", 16));
        options.cacheSize(LQPConfiguration.getInstance().getConfiguration()
            .getInt("leads.processor.infinispan.leveldb.cachesize", 256));
        try {
            keysDB = factory.open(keydbFile,options);
            dataDB = factory.open(datadbFile,options);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Iterable<Map.Entry<String,Integer>> getKeysIterator(){
        return new LevelDBIterator(keysDB);
    }
    public Iterator<Object> getKeyIterator(String key , Integer counter){
//        if(iterator == null){
//            iterator = new BerkeleyDBIterator(indexDB,key);
//        }
//        iterator.initialize(key);
//        return iterator;
        return null;
    }

    public static void main(String[] args) {
        BerkeleyDBIndex index = new BerkeleyDBIndex("/tmp/testdb/","mydb");
        initTuple();
        int numberofkeys = 1000;
        int numberofvalues = 200;
        String baseKey= "baseKeyString";

        long start = System.nanoTime();

        for(int key = 0; key < numberofkeys; key++){
            //            System.out.println("key " + key);
            for(int value =0; value < numberofvalues; value++){
                index.add(baseKey+key,getTuple(key,value));
            }
        }
        long end = System.nanoTime();
        long dur = end - start;
        dur /= 1000000;
        int total = numberofkeys*numberofvalues;
        double avg = total/(double)dur;

        System.out.println("Put " + (total) + " in " + (dur) + " avg " + avg);
        int counter  =0;
        start = System.nanoTime();
        //        for(int key = 0; key < numberofkeys; key++) {
        for(Map.Entry<String,Integer> entry : index.getKeysIterator()){
            counter = 0;
            Iterator<Object> iterator = index.getKeyIterator(entry.getKey(),numberofvalues);
            while(iterator.hasNext()){
                Tuple t = (Tuple) iterator.next();
                counter++;
            }
            if(counter != numberofvalues){
                System.err.println("Iteration failed for key " + entry.getKey());
            }
        }
        end = System.nanoTime();
        dur = end - start;
        dur /= 1000000;
        avg = total/(double)dur;
        System.out.println("Iterate " + (total) + " in " + (dur) + " avg " + avg);
        index.close();
        System.out.println("exit---");
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

        t = new Tuple();
        //        int key = 4;
        //        int value = 5;
        for(int i = 0 ; i < 10; i++){
            t.setAttribute("key-"+key+"-"+i,key);
            t.setAttribute("value-"+value+"-"+i,value);
            t.setAttribute("keyvalue-"+key+"."+value+"-"+i,key*value);
        }
        return t;
    }

    public void put(Object key,Object value){
        add(key,value);
    }
    public void add(Object keyObject , Object valueObject){
        String key = null;
        Tuple value = null;




    }

    private DatabaseEntry getDBValue(Object valueObject) {
//        DatabaseEntry result = new DatabaseEntry();
//        Object value = null;
//        if(valueObject instanceof TupleWrapper){
//            value = (TupleWrapper)valueObject;
//        }
//        else if (valueObject instanceof Tuple){
//            value = valueObject;
//        }
//        else {
//
//            System.err.println("value class of " + valueObject.getClass().toString());
//        }
//        tupleBinding.objectToEntry(value,result);
//        return result;
        return null;
    }

    private DatabaseEntry getDBKey(Object keyObject) {
//
//        String key = null;
//        if(keyObject instanceof ComplexIntermediateKey){
//            key = ((ComplexIntermediateKey)keyObject).asString();
//        }
//        else{
//            //                System.err.println("key class of " + keyObject.getClass().toString());
//            key = keyObject.toString();
//        }
//        DatabaseEntry result = new DatabaseEntry(key.getBytes(Charset.forName("UTF-8")));
        return null;
//        return result;
    }

    public void close() {
//        if(iterator != null)
//        {
//            iterator.close();
//        }
//        if(keyIterable != null){
//            keyIterable.close();
//        }
//        if (indexDB != null) {
//            try {
//                indexDB.close();
//            } catch (DatabaseException dbe) {
//                System.err.println("Error closing store: " + dbe.toString());
//                System.exit(-1);
//            }
//        }
//
//        if (env != null) {
//            try {
//                // Finally, close environment.
//                env.close();
//            } catch (DatabaseException dbe) {
//                System.err.println("Error closing MyDbEnv: " + dbe.toString());
//                System.exit(-1);
//            }
//        }
//        dbFile = new File(dbFile.toString()+"/");
//        for(File f : dbFile.listFiles())
//        {
//            f.delete();
//        }
//        dbFile.delete();

    }
}

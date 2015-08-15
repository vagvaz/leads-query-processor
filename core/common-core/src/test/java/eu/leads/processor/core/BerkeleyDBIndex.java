package eu.leads.processor.core;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by vagvaz on 8/14/15.
 */
public class BerkeleyDBIndex {
    private EnvironmentConfig environmentConfig;
    private File dbFile;
    private EntityStore store;
    Environment env;
    StoreConfig dbConfig;
    Database indexDB;
    TupleBinding tupleBinding;
    PrimaryIndex<String, TupleWrapper> primaryIndex;
    SecondaryIndex<String, String, TupleWrapper> secondaryIndex;
    BerkeleyDBIterator iterator = null;
    public BerkeleyDBIndex(String baseDir, String dbName) {
        environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setLocking(false);
        environmentConfig.setTransactional(false);
        dbConfig = new StoreConfig();
        dbConfig.setTransactional(false);
        dbConfig.setAllowCreate(true);
        dbFile = new File(baseDir);
        dbFile.mkdirs();
        try {
            env = new Environment(new File(baseDir), environmentConfig);
            store = new EntityStore(env, dbName, dbConfig);
            primaryIndex = store.getPrimaryIndex(String.class, TupleWrapper.class);
            secondaryIndex = store.getSecondaryIndex(primaryIndex, String.class, "key");
            //            indexDB = env.openDatabase(null,dbName,dbConfig);
        } catch (DatabaseException e) {
            e.printStackTrace();
            return;
        }
    }

    public void add(String key, int counter, Tuple tuple) {
        TupleWrapper tupleWrapper = new TupleWrapper(key, counter, tuple);
        try {
            primaryIndex.put(null, tupleWrapper);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
    public Iterator<Object> getKeyIterator(String key , Integer counter){
        if(iterator != null){
            iterator = new BerkeleyDBIterator(secondaryIndex,key);
        }
        else{
            iterator.initialize(key);
        }
        return iterator;
    }

    public static void main(String[] args) {
        BerkeleyDBIndex index = new BerkeleyDBIndex("/tmp/testdb/","mydb");
        int numberofkeys = 10;
        int numberofvalues = 50000;
        String baseKey= "baseKeyString";

        for(int key = 0; key < numberofkeys; key++){
            for(int value =0; value < numberofvalues; value++){
                index.add(baseKey,value,getTuple(key,value));
            }
        }
        int counter  =0;
        for(int key = 0; key < numberofkeys; key++) {
            Iterator<Object> iterator = index.getKeyIterator(baseKey+key,numberofvalues);
            while(iterator.hasNext()){
                Tuple t = (Tuple) iterator.next();
                counter++;
            }
            if(counter != numberofvalues){
                System.err.println("Iteration failed for key " + key);
            }
        }
        System.out.println("exit---");
    }

    private static Tuple getTuple(int key, int value) {
        Tuple t = new Tuple();
        for(int i = 0 ; i < 10; i++){
            t.setAttribute("key-"+key+"-"+i,key);
            t.setAttribute("value-"+value+"-"+i,value);
            t.setAttribute("keyvalue-"+key+"."+value+"-"+i,key*value);
        }
        return t;
    }

    //    public void add(Object keyObject , Object valueObject){
    //        String key = null;
    //        Tuple value = null;
    //
    //
    //
    //        DatabaseEntry keyEntry = getDBKey(keyObject);
    //        DatabaseEntry valueEntry = getDBValue(valueObject);
    //        try {
    //            indexDB.put(null,keyEntry,valueEntry);
    //        } catch (DatabaseException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    private DatabaseEntry getDBValue(Object valueObject) {
    //        DatabaseEntry result = new DatabaseEntry();
    //        TupleWrapper value = null;
    //        if(valueObject instanceof TupleWrapper){
    //            value = (TupleWrapper)valueObject;
    //        }
    //        else {
    //
    //            System.err.println("value class of " + valueObject.getClass().toString());
    //        }
    //
    //        tupleBinding.objectToEntry(value,result);
    //        return result;
    //    }
    //
    //    private DatabaseEntry getDBKey(Object keyObject) {
    //
    //        String key = null;
    //        if(keyObject instanceof String){
    //            key = (String)keyObject;
    //        }
    //        else{
    //            System.err.println("key class of " + keyObject.getClass().toString());
    //            key = keyObject.toString();
    //        }
    //        DatabaseEntry result = new DatabaseEntry(key.getBytes(Charset.defaultCharset()));
    //        return result;
    //    }
    //
    //    public Object getIterator(String key){
    //        try {
    //            SecondaryDatabase secondaryDatabase =
    //                (SecondaryDatabase) indexDB.getSecondaryDatabases().get(0);
    //            secondaryDatabase.
    //        } catch (DatabaseException e) {
    //            e.printStackTrace();
    //        }
    //    }
    public void close() {
        if (store != null) {
            try {
                store.close();
            } catch (DatabaseException dbe) {
                System.err.println("Error closing store: " + dbe.toString());
                System.exit(-1);
            }
        }

        if (env != null) {
            try {
                // Finally, close environment.
                env.close();
            } catch (DatabaseException dbe) {
                System.err.println("Error closing MyDbEnv: " + dbe.toString());
                System.exit(-1);
            }
        }
    }
}

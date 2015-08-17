package eu.leads.processor.core;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.infinispan.ComplexIntermediateKey;
import org.bson.BSONEncoder;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public  void printKeys(){
        DBIterator iterator = keysDB.iterator();
        while(iterator.hasNext()){
            System.out.println(asString(iterator.next().getKey()));
        }

        iterator = dataDB.iterator();
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
        if(valuesIterator == null){
            valuesIterator = new LevelDBDataIterator(dataDB,key,counter);
        }

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

//        index.printKeys();

        start = System.nanoTime();
        //        for(int key = 0; key < numberofkeys; key++) {
        for(Map.Entry<String,Integer> entry : index.getKeysIterator()){
            counter = 0;
//            System.out.println("iter key "+entry.getKey());
            Iterator<Object> iterator = index.getKeyIterator(entry.getKey(),entry.getValue());
            while(true){
                try {
                    Tuple t = (Tuple) iterator.next();
                    //                System.out.println(t.getAttribute("key")+" --- " + t.getAttribute("value"));
                    counter++;
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
        t.setAttribute("key",Integer.toString(key));
        t.setAttribute("value",Integer.toString(value));
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
        keysDB.put(bytes(key+"{}"), keyvalue);
        BSONEncoder encoder = new BasicBSONEncoder();
        dataDB.put(bytes(key+"{}"+counter),encoder.encode(value.asBsonObject()));
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
                dataDB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        baseDirFile = new File(baseDirFile.toString()+"/");
        for(File f : baseDirFile.listFiles())
        {
            f.delete();
        }
        baseDirFile.delete();

    }
}

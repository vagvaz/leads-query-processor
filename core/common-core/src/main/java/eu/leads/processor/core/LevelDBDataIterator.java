package eu.leads.processor.core;

import org.bson.BasicBSONDecoder;
import org.iq80.leveldb.*;
import org.fusesource.leveldbjni.JniDBFactory;
import org.fusesource.leveldbjni.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by vagvaz on 8/17/15.
 */
public class LevelDBDataIterator implements Iterator<Object> {
    DB data;
    String key;
    Integer total;
    int currentCounter;
    DBIterator iterator;
    public LevelDBDataIterator(DB dataDB, String key, Integer counter) {
        this.data = dataDB;
        this.key = key;
        this.total = counter;
    }

    @Override public boolean hasNext() {
        if(currentCounter <= total){
            return true;
        }
        return false;
    }

    @Override public Object next() {
        if(currentCounter <= total){
            Map.Entry<byte[],byte[]> entry = iterator.next();
            if(validateKey(entry.getKey())){
                BasicBSONDecoder decoder = new BasicBSONDecoder();
                Tuple result = new Tuple(decoder.readObject(entry.getValue()));
                currentCounter++;
                return result;
            }
            else{
                throw new NoSuchElementException("Counter " + currentCounter + " total " + total + " but next returned key " + getKey(entry.getKey()));
            }
        }
        throw new NoSuchElementException("Leveldb Iterator no more values");
    }

    private boolean validateKey(byte[] key) {
        String keyString = getKey(key);
        if(this.key.equals(keyString.split("\\{\\}")[0])){
            return true;
        }
        return false;
    }

    private String getKey(byte[] key) {
        String result = new String(key);
        return result.split("\\{\\}")[0];
    }

    @Override public void remove() {

    }

    public void initialize(String key,int tot) {
        this.key = key;
        this.total =tot;
        this.currentCounter = 0;
        if(iterator == null){
            iterator = data.iterator();
            Map.Entry<byte[],byte[]> entry = iterator.peekNext();
            if(!validateKey(entry.getKey())){
                System.out.println("Unsuccessful for key " + this.key + " was " + new String(entry.getKey()));
                String searchKey = key + "{}";
                iterator.seek(searchKey.getBytes());
            }
            return;
        }
        Map.Entry<byte[],byte[]> entry = iterator.peekNext();
        if(!validateKey(entry.getKey())){
            System.out.println("Unsuccessful for key " + this.key + " was " + new String(entry.getKey()));
            String searchKey = key + "{}";
            iterator.seek(searchKey.getBytes());
        }


    }

    public void close() {
        try {
            iterator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

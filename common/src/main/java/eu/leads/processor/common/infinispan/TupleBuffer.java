package eu.leads.processor.common.infinispan;

import eu.leads.processor.core.Tuple;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 8/30/15.
 */
public class TupleBuffer implements Serializable {
    Map<String,Tuple> buffer;
    private  transient int threshold;
    private transient EnsembleCacheManager emanager;
    private transient EnsembleCache ensembleCache;
    private transient long localCounter;

    public TupleBuffer(){
        buffer = new HashMap<>();
        threshold = 500;
        localCounter = 0;
    }
    public TupleBuffer(byte[] bytes){
        BSONDecoder decoder = new BasicBSONDecoder();
        buffer = new HashMap<>();
        //        int compressedSize = in.readInt();
        byte[] compressed = bytes;//new byte[compressedSize];
        try {
            byte[] uncompressed = Snappy.uncompress(compressed);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(uncompressed);
            ObjectInputStream inputStream = new ObjectInputStream(byteStream);
            int size = inputStream.readInt();
            for (int index = 0; index < size; index++) {
                String key = inputStream.readUTF();
//                int tupleBytesSize = inputStream.readInt();
//                byte[] tupleBytes = new byte[tupleBytesSize];
//                inputStream.read(tupleBytes);
//                Tuple tuple = new Tuple(decoder.readObject(tupleBytes));
                Tuple tuple = (Tuple) inputStream.readObject();
                buffer.put(key, tuple);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public TupleBuffer(int threshold){
        buffer = new HashMap<>();
        this.threshold = threshold;
        localCounter = 0;
    }

    public TupleBuffer(int threshold, EnsembleCache cache, EnsembleCacheManager ensembleCacheManager) {
        this.threshold = threshold;
        buffer = new HashMap<>();
        this.emanager = ensembleCacheManager;
        this.ensembleCache = emanager.getCache(cache.getName()+".compressed", new ArrayList<>(ensembleCacheManager.sites()),
            EnsembleCacheManager.Consistency.DIST);
        localCounter = 0;
    }

    public Map getBuffer(){
        return buffer;
    }

    public boolean add(String key, Tuple value){
        buffer.put(key,value);
        return (buffer.size() >= threshold);
    }

    private void writeObject(java.io.ObjectOutputStream out)
        throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
        BSONEncoder encoder = new BasicBSONEncoder();
        outputStream.writeInt(buffer.size());
        for(Map.Entry<String,Tuple> entry : buffer.entrySet()){
            outputStream.writeUTF(entry.getKey());
            byte[] tupleBytes = encoder.encode(entry.getValue().asBsonObject());
            outputStream.writeInt(tupleBytes.length);
            outputStream.write(tupleBytes);
        }
        outputStream.flush();
        byte[] uncompressed = byteStream.toByteArray();
        byte[] compressed = Snappy.compress(uncompressed);
//        out.writeInt(compressed.length);
//        out.write(compressed);
        out.writeObject(compressed);
    }
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException{
        BSONDecoder decoder = new BasicBSONDecoder();
        buffer = new HashMap<>();
//        int compressedSize = in.readInt();
        byte[] compressed = (byte[]) in.readObject();//new byte[compressedSize];
        in.read(compressed);
        byte[] uncompressed = Snappy.uncompress(compressed);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(uncompressed);
        ObjectInputStream inputStream = new ObjectInputStream(byteStream);
        int size = inputStream.readInt();
        for (int index = 0; index < size; index++) {
            String key =  inputStream.readUTF();
            int tupleBytesSize = inputStream.readInt();
            byte[] tupleBytes =  new byte[tupleBytesSize];
            inputStream.read(tupleBytes);
            Tuple tuple = new Tuple(decoder.readObject(tupleBytes));
            buffer.put(key,tuple);
        }
    }
    private void readObjectNoData()
        throws ObjectStreamException{
        buffer = new HashMap<>();
        threshold = 500;
    }

    public void flushToMC() {
        localCounter = (localCounter+1)%Long.MAX_VALUE;
        byte[] bytes= this.serialize();
        ensembleCache.put(Long.toString(localCounter),bytes);
        buffer.clear();
    }

    private byte[] serialize()  {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
            BSONEncoder encoder = new BasicBSONEncoder();
            outputStream.writeInt(buffer.size());
            for (Map.Entry<String, Tuple> entry : buffer.entrySet()) {
                outputStream.writeUTF(entry.getKey());
//                byte[] tupleBytes = encoder.encode(entry.getValue().asBsonObject());
//                outputStream.writeInt(tupleBytes.length);
//                outputStream.write(tupleBytes);
                outputStream.writeObject(entry.getValue());
            }
            outputStream.flush();
            byte[] uncompressed = byteStream.toByteArray();
            byte[] compressed = Snappy.compress(uncompressed);
            //        out.writeInt(compressed.length);
            //        out.write(compressed);
            return compressed;
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }
}

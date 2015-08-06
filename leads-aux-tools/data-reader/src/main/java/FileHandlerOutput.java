import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.nutch.storage.WebPage;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 01/08/15.
 */
public class FileHandlerOutput<K  extends Serializable,V> implements OutputHandler<K,V> {
    long valuesThreshold;
    long byteThreshold;
    long byteSize;
    long totalByteSize;
    long totalValues;
    long valueSize;
    String baseDir;
    int fileCounter;
    String filename;
    ObjectOutputStream keyWriter;
    ObjectOutputStream dataWriter;
    DataFileWriter valueWriter;
    boolean storeNutchData = false;
    @Override public void initialize(Properties conf) {
        if(conf.containsKey("valueThreshold")){
            valuesThreshold = Integer.parseInt(conf.getProperty("valueThreshold"));
        } else{
            valuesThreshold = 100000;
        }

        if(conf.containsKey("byteThreshold")){
            byteThreshold = Integer.parseInt(conf.getProperty("byteThreshold"));
        } else {
            byteThreshold = 300 * 1024*1024; //300mb
        }

        if(conf.containsKey("baseDir")){
            baseDir = conf.getProperty("baseDir");
        }
        else{
            baseDir = "/tmp/leads/defaultDataOut";
        }

        if(conf.containsKey("filename")){
            filename = conf.getProperty("filename");
        }  else{
            filename = "leads-crawling-data";
        }
        if(conf.containsKey("nutchData")){
            storeNutchData = Boolean.parseBoolean(conf.getProperty("nutchData"));
        }
        fileCounter = 0;
        try {
            File keyFile = new File(baseDir+"/"+filename+"."+fileCounter+".keys");
            File valueFile = new File(baseDir+"/"+filename+"."+fileCounter+".values");
            keyFile.getParentFile().mkdirs();
            keyFile.createNewFile();
            valueFile.createNewFile();
            keyWriter =  new ObjectOutputStream( new FileOutputStream(keyFile));
            if(storeNutchData) {
                valueWriter = new DataFileWriter( new GenericDatumWriter());//  new GenericDatumWriter(WebPage.SCHEMA$));
                valueWriter.create(WebPage.SCHEMA$, valueFile);
            } else {
                dataWriter = new ObjectOutputStream(
                    new FileOutputStream(valueFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public long putAll(Map<K, V> data) {
        for(Map.Entry<K,V> entry : data.entrySet()){
            append(entry.getKey(),entry.getValue());
        }

        return byteSize;
    }

    @Override public long append(K key, V value) {
        if(value instanceof  GenericData.Record || value instanceof WebPage)
        {
            GenericData.Record genericValue;
            if(value instanceof WebPage){
                genericValue = new GenericData.Record(WebPage.SCHEMA$);
                for(int i =0 ; i < genericValue.getSchema().getFields().size();i++){
                    genericValue.put(i, ((WebPage) value).get(i));
                }
            }
            else{
                genericValue = (GenericData.Record) value;
            }
            GenericData.Record page = (GenericData.Record) genericValue;
            byte[] keyBytes = key.toString().getBytes();
            try {
                keyWriter.writeUTF(key.toString()+"\n");
                valueWriter.append(value);
                valueSize++;
                long oldByteSize = byteSize;
                byteSize = valueWriter.sync();
                totalByteSize += (byteSize-oldByteSize);
                totalValues++;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            try {
                keyWriter.writeObject(key);
                dataWriter.writeObject(value);
                totalValues++;
                valueSize++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(byteSize >= byteThreshold || valueSize >= valuesThreshold){
            rollFile();
        }
        return totalByteSize;
    }

    private void rollFile() {
        fileCounter++;

        try {
            File keyFile = new File(baseDir+"/"+filename+"."+fileCounter+".keys");
            File valueFile = new File(baseDir+"/"+filename+"."+fileCounter+".values");
            keyFile.createNewFile();
            valueFile.createNewFile();

            keyWriter.close();
            keyWriter = new ObjectOutputStream( new FileOutputStream(baseDir+"/"+filename+"."+fileCounter+".keys"));
            if(storeNutchData){
                valueWriter.close();
                valueWriter.create(WebPage.SCHEMA$,
                    new File(baseDir + "/" + filename + "." + fileCounter + ".values"));
            }
            else{
                dataWriter.close();
                dataWriter = new ObjectOutputStream( new FileOutputStream(baseDir+"/"+filename+"."+fileCounter+".values"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        byteSize = 0;
        valueSize = 0;
    }

    @Override public void close() {
        try {
            if(keyWriter!= null){
                keyWriter.close();
            }
            if (storeNutchData) {
                valueWriter.close();
            } else {
                if(valueWriter!= null)
                    dataWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

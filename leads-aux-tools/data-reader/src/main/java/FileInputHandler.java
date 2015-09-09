import eu.leads.processor.conf.PatternFileNameFilter;
import eu.leads.processor.core.Tuple;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.nutch.storage.WebPage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 02/08/15.
 */
public class FileInputHandler<K extends Serializable, V> implements InputHandler<K, V> {
    String baseDir;
    String prefix;
    K defaultKey;
    V defaultValue;
    long limit;
    long batchSize;
    DataFileReader nutchDataReader;
    ObjectInputStream valueReader;
    ObjectInputStream keyReader;
    File[] files;
    int currentFile = 0;
    long numberOfValues;

    @Override public void initialize(Properties conf) {
        if (conf.containsKey("limit")) {
            limit = Long.parseLong(conf.getProperty("limit"));
        } else {
            limit = Long.MAX_VALUE;
        }

        if (conf.containsKey("batchSize")) {
            batchSize = Long.parseLong("batchSize");
        } else {
            batchSize = 10000;
        }

        if (conf.containsKey("keyClass")) {
            Class<K> keyClass = (Class<K>) conf.get("keyClass");
            try {
                defaultKey = keyClass.getConstructor().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            defaultKey = (K) new String();
        }

        if (conf.containsKey("valueClass")) {
            defaultValue = (V) conf.get("valueClass");
        } else {
            defaultValue = (V) new Tuple();
        }
        if (conf.containsKey("baseDir")) {
            baseDir = conf.getProperty("baseDir");
        } else {
            baseDir = "/tmple/leads/defaultDir/";
        }

        if (conf.containsKey("prefix")) {
            prefix = conf.getProperty("prefix") + "*keys";
        } else {
            prefix = "*keys";
        }

        File dir = new File(baseDir);
        files = dir.listFiles(new PatternFileNameFilter(prefix));
        String absolutePath = files[currentFile].getAbsolutePath().toString();
        String baseName = absolutePath.substring(0, absolutePath.lastIndexOf("."));
        try {
            if (files.length > 0) {
                keyReader = new ObjectInputStream(new FileInputStream(files[currentFile]));
                if (defaultValue instanceof WebPage || defaultValue instanceof GenericData.Record) {
                    nutchDataReader =
                        new DataFileReader(new File(baseName + ".values"), new GenericDatumReader(WebPage.SCHEMA$));
                } else {
                    valueReader = new ObjectInputStream(new FileInputStream(baseName + ".values"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentFile++;

    }

    @Override public Map<K, V> getAll() {
        Map<K, V> result = new HashMap<>();
        while (hasNext()) {
            Map.Entry<K, V> entry = next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override public Map<K, V> getNextBatch(long offset) {
        Map<K, V> result = new HashMap<>();
        long counter = 0;
        while (hasNext()) {
            if (counter >= batchSize)
                break;
            Map.Entry<K, V> entry = next();
            result.put(entry.getKey(), entry.getValue());
            counter++;
        }
        ;
        return result;
    }

    @Override public void setBatchSize(long batchSize) {
        this.batchSize = batchSize;
    }

    @Override public long getBatchSize() {
        return batchSize;
    }

    @Override public void close() {
        try {
            keyReader.close();
            if (nutchDataReader != null) {
                nutchDataReader.close();
            }
            if (valueReader != null) {
                valueReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override public boolean hasNext() {
        boolean result = false;
        if (numberOfValues >= limit) {
            return false;
        }
        if (currentFile < files.length) {
            result = true;
        } else {
            try {
                if (keyReader.available() > 0) {
                    result = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override public Map.Entry<K, V> next() {
        K key;
        V value;
        if (numberOfValues >= limit) {
            return null;
        }
        try {
            if (keyReader.available() == 0) {
                if (currentFile < files.length) {
                    useFile(files[currentFile]);
                    currentFile++;
                    return next();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (defaultValue instanceof WebPage || defaultValue instanceof GenericData.Record) {
                //                int byteSize = keyReader.readInt();
                //                byte[] bytes = new byte[byteSize];
                //                keyReader.readFully(bytes);
                key = (K) keyReader.readUTF().trim();
                value = (V) nutchDataReader.next();
                numberOfValues++;
                return new AbstractMap.SimpleEntry<K, V>(key, value);
            } else {
                key = (K) keyReader.readObject();
                value = (V) valueReader.readObject();
                numberOfValues++;
                return new AbstractMap.SimpleEntry<K, V>(key, value);
            }
        } catch (EOFException eof) {


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }

    private void useFile(File file) {
        String absolutePath = file.getAbsolutePath().toString();
        String baseName = absolutePath.substring(0, absolutePath.lastIndexOf("."));
        try {
            keyReader = new ObjectInputStream(new FileInputStream(file));
            if (defaultValue instanceof WebPage || defaultValue instanceof GenericData.Record) {
                nutchDataReader =
                    new DataFileReader(new File(baseName + ".values"), new GenericDatumReader(WebPage.SCHEMA$));
            } else {
                valueReader = new ObjectInputStream(new FileInputStream(baseName + ".values"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Do not Remove from FileInputHandler");
    }
}

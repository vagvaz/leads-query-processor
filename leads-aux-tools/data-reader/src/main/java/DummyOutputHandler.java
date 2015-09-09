import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 01/08/15.
 */
public class DummyOutputHandler<K, V> implements OutputHandler<K, V> {
    @Override public void initialize(Properties conf) {

    }

    @Override public long putAll(Map<K, V> data) {
        long size = 0;
        for (Map.Entry<K, V> entry : data.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            System.out.println("key: " + key + " -> " + value);
            size += key.length() + value.length();
        }

        return size;
    }

    @Override public long append(K key, V value) {
        String keyString = key.toString();
        String valueString = value.toString();
        System.out.println("key: " + keyString + " -> " + valueString);
        return keyString.length() + valueString.length();
    }

    @Override public void close() {

    }
}

import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 01/08/15.
 */
public interface OutputHandler<K,V> {

    void initialize(Properties conf);
    long putAll(Map<K,V> data);
    long append(K key, V value);
    void close();
}
